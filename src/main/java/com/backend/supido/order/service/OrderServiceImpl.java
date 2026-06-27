package com.backend.supido.order.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.common.utils.JwtValidator;
import com.backend.supido.common.utils.RestaurantUtils;
import com.backend.supido.coupon.domain.entity.Coupon;
import com.backend.supido.coupon.repository.CouponRepository;
import com.backend.supido.deliveryPerson.domain.dto.response.DeliveryPersonResponse;
import com.backend.supido.deliveryPerson.domain.entity.DeliveryPerson;
import com.backend.supido.deliveryPerson.repository.DeliveryPersonRepository;
import com.backend.supido.deliveryPerson.service.DeliveryPersonService;
import com.backend.supido.googleMaps.dto.RouteResult;
import com.backend.supido.googleMaps.service.GoogleMapsService;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.menuItem.repository.MenuItemRepository;
import com.backend.supido.notification.domain.enums.NotificationType;
import com.backend.supido.notification.service.NotificationService;
import com.backend.supido.claim.service.ClaimService;
import com.backend.supido.order.common.enums.Status;
import com.backend.supido.order.common.mappers.OrderMapper;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderReceiptResponse;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.dto.response.OrderStatsResponse;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.orderItem.domain.entity.OrderItem;
import com.backend.supido.orderItem.mapper.OrderItemMapper;
import com.backend.supido.orderItem.repository.OrderItemRepository;
import com.backend.supido.payment.service.PaymentService;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import com.backend.supido.user.domain.entity.User;
import com.backend.supido.user.repository.UserRepository;
import com.backend.supido.user.service.UserServiceImpl;
import com.backend.supido.userAddress.domain.entity.UserAddress;
import com.backend.supido.userAddress.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final CouponRepository couponRepository;
    private final NotificationService notificationService;
    private final DeliveryPersonService deliveryPersonService;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final PaymentService paymentService;
    private final ClaimService claimService;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final GoogleMapsService googleMapsService;

    @Transactional
    @Override //Al crear la orden no te sale el arreglo de items
    public OrderResponse create(CreateOrderRequest request, User user) {
       userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: "));
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.restaurantId()));

        if (!RestaurantUtils.isOpen(restaurant)) {
            throw new IllegalArgumentException("Restaurant is currently closed");
        }

        // direccion
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(request.userAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress not found"));

        Order order = OrderMapper.toEntityCreate(request, restaurant, userAddress, user);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setSubtotal(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);
        order.setShippingCost(BigDecimal.ZERO);
        order.setTotal(BigDecimal.ZERO);

        Order saved = orderRepository.save(order);

        // Procesar items
        List<OrderItem> orderItems = request.items().stream().map(itemRequest -> {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + itemRequest.menuItemId()));
            if (!menuItem.getAvailable()) {
                throw new IllegalArgumentException("MenuItem with id: " + itemRequest.menuItemId() + " is not available");
            }
            return OrderItemMapper.toEntityCreate(itemRequest, saved, menuItem);
        }).collect(Collectors.toList());


        orderItemRepository.saveAll(orderItems);

        // Calcular subtotal
        BigDecimal subtotal = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        saved.setSubtotal(subtotal);

        // Aplicar cupón si existe
        if (request.couponId() != null) {
            BigDecimal discount = applyCoupon(request.couponId(), subtotal);
            saved.setDiscount(discount);
        }
        // Calcular total con lo que tenemos por ahora (sin shippingCost todavia)
        BigDecimal tip = request.tip() != null ? request.tip() : BigDecimal.ZERO;
        BigDecimal discount = saved.getDiscount() != null ? saved.getDiscount() : BigDecimal.ZERO;
        saved.setTip(tip);
        saved.setTotal(subtotal.subtract(discount).add(tip));

        Order finalOrder = orderRepository.save(saved);

        // crear pago
        paymentService.createForOrder(finalOrder, request.paymentMethod(), finalOrder.getTotal());

        // crear notificacion
        notificationService.sendOrderNotification(user.getId(), finalOrder.getId(), NotificationType.ORDER_RECEIVED,
                "Your order has been received. The restaurant is processing it.");

        return OrderMapper.toDto(finalOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request,User user) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if(!existing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("This user has no permission to update this order");
        }

        Order updated = OrderMapper.toEntityUpdate(request);
        updated.setId(existing.getId());
        updated.setUser(existing.getUser());
        updated.setRestaurant(existing.getRestaurant());
        updated.setSubtotal(existing.getSubtotal());
        updated.setShippingCost(existing.getShippingCost());
        updated.setCreatedAt(existing.getCreatedAt());

        if (request.deliveryPersonId() != null) {
            DeliveryPerson dp = deliveryPersonRepository.findById(request.deliveryPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryPerson not found with id: " + request.deliveryPersonId()));
            updated.setDeliveryPerson(dp);
        } else {
            updated.setDeliveryPerson(existing.getDeliveryPerson());
        }

        // cupón antes del cálculo
        if (request.couponId() != null) {
            if (existing.getCouponId() != null) {
                throw new IllegalArgumentException("Order already has a coupon applied");
            }
            BigDecimal discount = applyCoupon(request.couponId(), existing.getSubtotal());
            updated.setDiscount(discount);
            updated.setCouponId(request.couponId());
        }

        // recalcular total con discount ya actualizado
        BigDecimal tip = request.tip() != null ? request.tip() : existing.getTip() != null ? existing.getTip() : BigDecimal.ZERO;
        BigDecimal discount = updated.getDiscount() != null ? updated.getDiscount() : existing.getDiscount() != null ? existing.getDiscount() : BigDecimal.ZERO;
        BigDecimal subtotal = existing.getSubtotal() != null ? existing.getSubtotal() : BigDecimal.ZERO;

        updated.setTip(tip);
        updated.setDiscount(discount);
        updated.setTotal(subtotal.subtract(discount).add(tip));

        return OrderMapper.toDto(orderRepository.save(updated));

    }

    @Transactional
    @Override
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Order can only be cancelled when in PENDING status");
        }
        order.setStatus(Status.CANCELLED);

        Order saved = orderRepository.save(order);

        // actualizar pago
        paymentService.cancelPayment(saved.getId());

        // crear notificacion
        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(),
                NotificationType.ORDER_CANCELLED, "Your order was cancelled free of charge.");
    }

    @Override
    public OrderResponse confirm(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Order must be in PENDING status to confirm");
        }
        order.setStatus(Status.CONFIRMED);

        // crear notificacion
        Order saved = orderRepository.save(order);

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(), NotificationType.ORDER_CONFIRMED,
                "The restaurant accepted your order and will start preparing it soon.");

        List<Long> nearbyDeliveryPersons = deliveryPersonService.findNearbyAvailableUserIds(
                saved.getRestaurant().getLatitude(), saved.getRestaurant().getLongitude(), 3.0);
        notificationService.notifyNewOrderToDeliveryPersons(nearbyDeliveryPersons, saved.getId());

        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponse prepare(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals(Status.CONFIRMED)) {
            throw new IllegalArgumentException("Order must be in CONFIRMED status to prepare");
        }
        order.setStatus(Status.PREPARING);

        // crear notificacion
        Order saved = orderRepository.save(order);

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(),
                NotificationType.ORDER_PREPARING, "Your order is being prepared. Estimated time: 20-30 min.");

        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponse onTheWay(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals(Status.PREPARING)) {
            throw new IllegalArgumentException("Order must be in PREPARING status to go on the way");
        }
        order.setStatus(Status.ON_THE_WAY);

        // crear notificacion
        Order saved = orderRepository.save(order);

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(), NotificationType.ORDER_ON_THE_WAY,
                "Your order has been picked up by the delivery person and is on its way.");

        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals(Status.ON_THE_WAY)) {
            throw new IllegalArgumentException("Order must be in ON_THE_WAY status to deliver");
        }
        order.setStatus(Status.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);

        // crear notificacion
        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(),
                NotificationType.ORDER_DELIVERED, "Your order has been delivered! We hope you enjoy it.");
        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(),
                NotificationType.RATE_YOUR_ORDER, "Rate your experience: restaurant and delivery person.");

        return OrderMapper.toDto(saved);
    }

    @Transactional
    @Override
    public OrderResponse acceptOrder(Long id, User user) {
        if (!"ROLE_DRIVER".equals(user.getRole().getName())) {
            throw new IllegalArgumentException("Only delivery persons can accept orders");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.getStatus().equals(Status.CONFIRMED)) {
            throw new IllegalArgumentException("Order can only be accepted when it is CONFIRMED");
        }

        if (order.getDeliveryPerson() != null) {
            throw new IllegalArgumentException("This order has already been claimed by another driver");
        }

        DeliveryPersonResponse driverProfile = deliveryPersonService.findByUserId(user.getId());

        if (!driverProfile.available()) {
            throw new IllegalArgumentException("You must be set as available to accept orders");
        }

        return assignDeliveryPerson(id, driverProfile.id());
    }

    @Transactional
    @Override
    public OrderResponse assignDeliveryPerson(Long id, Long deliveryPersonId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.getStatus().equals(Status.CONFIRMED)) {
            throw new IllegalArgumentException("A delivery person can only be assigned when the order is CONFIRMED");
        }

        DeliveryPerson dp = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryPerson not found with id: " + deliveryPersonId));
        order.setDeliveryPerson(dp);

        RouteResult route = googleMapsService.computeRoute(
                dp.getLatitude(), dp.getLongitude(),
                order.getRestaurant().getLatitude(), order.getRestaurant().getLongitude(),
                order.getUserAddress().getLatitude(), order.getUserAddress().getLongitude()
        );

        double distanceKm = route.distanceMeters() / 1000.0;
        BigDecimal shippingCost = BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;
        BigDecimal tip = order.getTip() != null ? order.getTip() : BigDecimal.ZERO;

        order.setShippingCost(shippingCost);
        order.setTotal(subtotal.subtract(discount).add(tip).add(shippingCost));

        Order saved = orderRepository.save(order);

        notificationService.sendOrderNotification(saved.getUser().getId(), saved.getId(),
                NotificationType.DELIVERY_ASSIGNED, "A delivery person has been assigned to your order. Pickup is coming soon.");

        return OrderMapper.toDto(saved);
    }

    @Override
    public void confirmCashPayment(Long id, Long deliveryPersonId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getDeliveryPerson() == null || !order.getDeliveryPerson().getId().equals(deliveryPersonId)) {
            throw new IllegalArgumentException("This delivery person does not have permission to confirm a cash payment");
        }

        paymentService.completeCashPayment(order.getId());
    }
      
    @Override
    public OrderReceiptResponse getReceipt(Long id,User user) {
        Order existingOrder= orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        if(!existingOrder.getUser().getId().equals(user.getId())){
              throw new IllegalArgumentException("You do not have permission to do this action.");
        }
        OrderResponse order = OrderMapper.toDto(existingOrder);


        return OrderReceiptResponse.builder()
                .order(order)
                .payment(paymentService.findByOrderId(id, user))
                .claims(claimService.findByOrderId(id, user))
                .build();
    }

    @Override
    public PageableResponse<OrderResponse> findByUserId(Long userId, int page, int size) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, PageRequest.of(page, size));
        return buildPageableResponse(orderPage);
    }

    @Override
    public PageableResponse<OrderResponse> findByRestaurantId(Long restaurantId, int page, int size) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        Page<Order> orderPage = orderRepository.findByRestaurantId(restaurantId, PageRequest.of(page, size));
        return buildPageableResponse(orderPage);
    }

    @Override
    public PageableResponse<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId, int page, int size, User user) {
        if ("ROLE_DRIVER".equals(user.getRole().getName())) {
            DeliveryPersonResponse driverProfile = deliveryPersonService.findByUserId(user.getId());
            if (!driverProfile.id().equals(deliveryPersonId)) {
                throw new IllegalArgumentException("You are not authorized to view orders from another driver");
            }
        }
        Page<Order> orderPage = orderRepository.findByDeliveryPerson_Id(deliveryPersonId, PageRequest.of(page, size));
        return buildPageableResponse(orderPage);
    }
    @Transactional(readOnly = true)
    @Override
    public PageableResponse<OrderResponse> findDeliveredOrdersByDeliveryPersonId(Long deliveryPersonId, int page, int size, User user) {
        if ("ROLE_DRIVER".equals(user.getRole().getName())) {
            DeliveryPersonResponse driverProfile = deliveryPersonService.findByUserId(user.getId());
            if (!driverProfile.id().equals(deliveryPersonId)) {
                throw new IllegalArgumentException("You are not authorized to view orders from another driver");
            }
        }
        Page<Order> orderPage = orderRepository.findByDeliveryPerson_IdAndStatus(
                deliveryPersonId, Status.DELIVERED, PageRequest.of(page, size));
        return buildPageableResponse(orderPage);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderStatsResponse getOrderStats(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getDeliveryPerson() == null) {
            throw new IllegalArgumentException("No delivery person assigned to this order yet");
        }

        DeliveryPersonResponse driver = deliveryPersonService.findById(order.getDeliveryPerson().getId());

        RouteResult route = googleMapsService.computeRoute(
                driver.latitude(), driver.longitude(),
                order.getRestaurant().getLatitude(), order.getRestaurant().getLongitude(),
                order.getUserAddress().getLatitude(), order.getUserAddress().getLongitude()
        );

        double distanceKm = route.distanceMeters() / 1000.0;

        return OrderStatsResponse.builder()
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .durationSeconds(route.durationSeconds())
                .shippingCost(order.getShippingCost())
                .build();
    }

    //possible util
    private BigDecimal applyCoupon(Long couponId, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + couponId));
        if (!coupon.getActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Coupon has expired");
        }
        BigDecimal discount = subtotal.multiply(coupon.getValue()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        coupon.setActive(false);
        couponRepository.save(coupon);
        return discount;
    }

    private PageableResponse<OrderResponse> buildPageableResponse(Page<Order> orderPage) {
        return PageableResponse.<OrderResponse>builder()
                .content(orderPage.getContent()
                        .stream()
                        .map(OrderMapper::toDto)
                        .collect(Collectors.toList()))
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }
}
