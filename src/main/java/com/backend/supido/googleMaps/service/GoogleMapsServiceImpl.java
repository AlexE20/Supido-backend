package com.backend.supido.googleMaps.service;

import com.backend.supido.googleMaps.dto.RouteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoogleMapsServiceImpl implements GoogleMapsService {

    private static final String ROUTES_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
    private static final String FIELD_MASK = "routes.distanceMeters,routes.duration";

    private final String apiKey;
    private final RestClient restClient;

    public GoogleMapsServiceImpl(@Value("${google.maps.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    @Override
    public RouteResult computeRoute(double originLat, double originLng,
                                    double intermediateLat, double intermediateLng,
                                    double destLat, double destLng) {
        Map<String, Object> body = Map.of(
                "origin", waypoint(originLat, originLng),
                "destination", waypoint(destLat, destLng),
                "intermediates", List.of(waypoint(intermediateLat, intermediateLng)),
                "travelMode", "DRIVE",
                "routingPreference", "TRAFFIC_AWARE",
                "computeAlternativeRoutes", false,
                "routeModifiers", Map.of(
                        "avoidTolls", false,
                        "avoidHighways", false,
                        "avoidFerries", false
                ),
                "languageCode", "en-US",
                "units", "METRIC"
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(ROUTES_URL)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("routes")) {
            throw new RuntimeException("Google Maps API returned an empty response");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        if (routes == null || routes.isEmpty()) {
            throw new RuntimeException("Google Maps API returned no routes");
        }

        Map<String, Object> route = routes.get(0);
        int distanceMeters = ((Number) route.get("distanceMeters")).intValue();
        String durationStr = (String) route.get("duration");
        long durationSeconds = parseDurationSeconds(durationStr);

        return new RouteResult(distanceMeters, durationSeconds);
    }

    private Map<String, Object> waypoint(double lat, double lng) {
        return Map.of("location", Map.of("latLng", Map.of("latitude", lat, "longitude", lng)));
    }

    private long parseDurationSeconds(String duration) {
        if (duration == null || duration.isBlank()) return 0L;
        String numeric = duration.endsWith("s") ? duration.substring(0, duration.length() - 1) : duration;
        try {
            return Long.parseLong(numeric);
        } catch (NumberFormatException e) {
            log.warn("Could not parse duration '{}', defaulting to 0", duration);
            return 0L;
        }
    }
}