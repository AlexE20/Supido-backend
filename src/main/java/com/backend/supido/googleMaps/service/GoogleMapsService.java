package com.backend.supido.googleMaps.service;

import com.backend.supido.googleMaps.dto.RouteResult;

public interface GoogleMapsService {
    RouteResult computeRoute(double originLat, double originLng,
                             double intermediateLat, double intermediateLng,
                             double destLat, double destLng);
}