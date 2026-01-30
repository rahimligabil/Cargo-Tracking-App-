package com.gabil.kargo.delivery.dto;

import java.util.Locale;

import com.gabil.kargo.delivery.Delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocationResponse {

    private Double latitude;
    private Double longitude;
    private String navigationUrl;

    public static DeliveryLocationResponse of(Delivery delivery) {
        Double latitude = delivery.getLatitude();
        Double longitude = delivery.getLongitude();

        return new DeliveryLocationResponse(
                latitude,
                longitude,
                buildNavigationUrl(latitude, longitude)
        );
    }

    private static String buildNavigationUrl(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return String.format(
                Locale.US,
                "https://www.google.com/maps/dir/?api=1&destination=%f,%f&travelmode=driving",
                latitude,
                longitude
        );
    }
}
