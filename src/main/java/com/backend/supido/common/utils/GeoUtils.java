package com.backend.supido.common.utils;

public class GeoUtils {
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calcula la distancia en línea recta (no la distancia real por calle)
     * entre dos coordenadas geográficas, usando la fórmula de Haversine.
     *
     * Fórmula:
     *   a = sin²(Δlat / 2) + cos(lat1) · cos(lat2) · sin²(Δlng / 2)
     *   c = 2 · atan2( √a, √(1−a) )
     *   distancia = R · c        (R = radio de la Tierra ≈ 6371 km)
     *
     * Las funciones trigonométricas de Math (sin, cos, atan2) esperan
     * radianes, no grados, por eso se convierte con Math.toRadians()
     * antes de cualquier cálculo.
     */
    public static double calculateDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
