package com.backend.supido.common.utils;

import com.backend.supido.restaurant.domain.entity.Restaurant;
import java.time.LocalTime;

public class RestaurantUtils {

    public static boolean isOpen(Restaurant restaurant) {
        LocalTime now = LocalTime.now();
        LocalTime opening = restaurant.getOpeningTime();
        LocalTime closing = restaurant.getClosingTime();

        if (closing.isAfter(opening)) {
            return !now.isBefore(opening) && !now.isAfter(closing);
        } else {
            return !now.isBefore(opening) || !now.isAfter(closing);
        }
    }
}