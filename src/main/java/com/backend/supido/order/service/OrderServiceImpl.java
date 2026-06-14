package com.backend.supido.order.service;

import com.backend.supido.common.PageableResponse;
import com.backend.supido.common.utils.RestaurantUtils;
import com.backend.supido.coupon.domain.entity.Coupon;
import com.backend.supido.coupon.repository.CouponRepository;
import com.backend.supido.exceptions.ResourceNotFoundException;
import com.backend.supido.menuItem.domain.entity.MenuItem;
import com.backend.supido.menuItem.repository.MenuItemRepository;
import com.backend.supido.order.common.mappers.OrderMapper;
import com.backend.supido.order.domain.dto.request.CreateOrderRequest;
import com.backend.supido.order.domain.dto.request.UpdateOrderRequest;
import com.backend.supido.order.domain.dto.response.OrderResponse;
import com.backend.supido.order.domain.entity.Order;
import com.backend.supido.order.repository.OrderRepository;
import com.backend.supido.orderItem.domain.entity.OrderItem;
import com.backend.supido.orderItem.mapper.OrderItemMapper;
import com.backend.supido.orderItem.repository.OrderItemRepository;
import com.backend.supido.restaurant.domain.entity.Restaurant;
import com.backend.supido.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.restaurantId()));

        Order order = OrderMapper.toEntityCreate(request, restaurant);
        order.setStatus("PENDING");
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
            Coupon coupon = couponRepository.findById(request.couponId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + request.couponId()));
            if (!coupon.getActive()) {
                throw new IllegalArgumentException("Coupon is not active");
            }
            if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Coupon has expired");
            }
            BigDecimal discount = subtotal.multiply(coupon.getValue()
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            saved.setDiscount(discount);
        }
        // Calcular total con lo que tenemos por ahora (sin shippingCost todavia)
        BigDecimal tip = request.tip() != null ? request.tip() : BigDecimal.ZERO;
        BigDecimal discount = saved.getDiscount() != null ? saved.getDiscount() : BigDecimal.ZERO;
        saved.setTip(tip);
        saved.setTotal(subtotal.subtract(discount).add(tip));


        return OrderMapper.toDto(orderRepository.save(saved));
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Order updated = OrderMapper.toEntityUpdate(request);
        updated.setId(existing.getId());
        updated.setUserId(existing.getUserId());
        updated.setRestaurant(existing.getRestaurant());
        updated.setSubtotal(existing.getSubtotal());
        updated.setShippingCost(existing.getShippingCost());
        updated.setCreatedAt(existing.getCreatedAt());

        // recalcular total si cambia tip o discount
        BigDecimal tip = request.tip() != null ? request.tip() : existing.getTip() != null ? existing.getTip() : BigDecimal.ZERO;
        BigDecimal discount = existing.getDiscount() != null ? existing.getDiscount() : BigDecimal.ZERO;
        BigDecimal subtotal = existing.getSubtotal() != null ? existing.getSubtotal() : BigDecimal.ZERO;

        updated.setTip(tip);
        updated.setDiscount(discount);
        updated.setTotal(subtotal.subtract(discount).add(tip));

        return OrderMapper.toDto(orderRepository.save(updated));
    }

    @Override
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Order can only be cancelled when in PENDING status");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Override
    public OrderResponse confirm(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Order must be in PENDING status to confirm");
        }
        order.setStatus("CONFIRMED");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse prepare(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Order must be in CONFIRMED status to prepare");
        }
        order.setStatus("PREPARING");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse onTheWay(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("PREPARING")) {
            throw new IllegalArgumentException("Order must be in PREPARING status to go on the way");
        }
        order.setStatus("ON_THE_WAY");
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse deliver(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (!order.getStatus().equals("ON_THE_WAY")) {
            throw new IllegalArgumentException("Order must be in ON_THE_WAY status to deliver");
        }
        order.setStatus("DELIVERED");
        order.setDeliveredAt(LocalDateTime.now());
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse assignDeliveryPerson(Long id, Long deliveryPersonId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setDeliveryPersonId(deliveryPersonId);
        return OrderMapper.toDto(orderRepository.save(order));
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
    public PageableResponse<OrderResponse> findByDeliveryPersonId(Long deliveryPersonId, int page, int size) {
        Page<Order> orderPage = orderRepository.findByDeliveryPersonId(deliveryPersonId, PageRequest.of(page, size));
        return buildPageableResponse(orderPage);
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
