package com.gabil.kargo.delivery.dto;

import java.util.Locale;
import java.util.UUID;

import com.gabil.kargo.delivery.Delivery;
import com.gabil.kargo.delivery.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryListItemResponse {

    private UUID id;
    private String recipientName;
    private String addressLine;
    private DeliveryStatus status;
    private String initials;
    private String deliveredFeedback;
    private String undeliveredFeedback;
    private java.util.List<String> documentUrls;

    public static DeliveryListItemResponse of(Delivery delivery) {
        return new DeliveryListItemResponse(
                delivery.getId(),
                delivery.getRecipientName(),
                delivery.getAddressLine(),
                delivery.getStatus(),
                buildInitials(delivery.getRecipientName()),
                delivery.getDeliveredFeedback(),
                delivery.getUndeliveredFeedback(),
                delivery.getDocumentUrls() != null ? delivery.getDocumentUrls() : java.util.Collections.emptyList()
        );
    }

    private static String buildInitials(String recipientName) {
        if (recipientName == null || recipientName.isBlank()) {
            return "";
        }
        String[] parts = recipientName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }
}
