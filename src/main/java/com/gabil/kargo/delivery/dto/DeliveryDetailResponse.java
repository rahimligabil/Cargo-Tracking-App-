package com.gabil.kargo.delivery.dto;

import java.time.Instant;
import java.util.UUID;

import com.gabil.kargo.delivery.Delivery;
import com.gabil.kargo.delivery.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDetailResponse {

    private UUID id;
    private DeliveryStatus status;
    private DeliveryCustomerInfoResponse customer;
    private DeliveryLocationResponse location;
    private Instant createdAt;
    private Instant completedAt;
    private Instant failedAt;
    private String deliveredFeedback;
    private String undeliveredFeedback;
    private java.util.List<String> documentUrls;

    public static DeliveryDetailResponse of(Delivery delivery) {
        return new DeliveryDetailResponse(
                delivery.getId(),
                delivery.getStatus(),
                DeliveryCustomerInfoResponse.of(delivery),
                DeliveryLocationResponse.of(delivery),
                delivery.getCreatedAt(),
                delivery.getCompletedAt(),
                delivery.getFailedAt(),
                delivery.getDeliveredFeedback(),
                delivery.getUndeliveredFeedback(),
                delivery.getDocumentUrls() != null ? delivery.getDocumentUrls() : java.util.Collections.emptyList()
        );
    }
}
