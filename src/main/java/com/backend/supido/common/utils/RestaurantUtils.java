package com.backend.supido.common.utils;

import com.backend.supido.restaurant.domain.entity.Restaurant;
import java.time.LocalTime;

public class RestaurantUtils {

    public static boolean isOpen(Restaurant restaurant) {
        LocalTime now = LocalTime.now();
        LocalTime opening = LocalTime.parse(restaurant.getOpeningTime());
        LocalTime closing = LocalTime.parse(restaurant.getClosingTime());
        return !now.isBefore(opening) && !now.isAfter(closing);
    }
}
