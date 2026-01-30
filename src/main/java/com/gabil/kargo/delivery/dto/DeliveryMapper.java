package com.gabil.kargo.delivery.dto;

import org.springframework.stereotype.Component;
import com.gabil.kargo.delivery.Delivery;

@Component
public class DeliveryMapper {

    public DeliveryDto toDto(Delivery entity) {
        if (entity == null) return null;

        return DeliveryDto.builder()
                .id(entity.getId())
                .deliveryNo(entity.getDeliveryNo())
                .recipientName(entity.getRecipientName())
                .addressLine(entity.getAddressLine())
                .driverName(entity.getDriver() != null
                        ? entity.getDriver().getUserName() + " " + entity.getDriver().getUserSurname()
                        : null)
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    
    public DeliveryDetailDto toDetailDto(Delivery entity) {
        if (entity == null) return null;

        return DeliveryDetailDto.builder()
                .id(entity.getId())
                .deliveryNo(entity.getDeliveryNo())
                .recipientName(entity.getRecipientName())
                .recipientPhone(entity.getRecipientPhone())
                .addressLine(entity.getAddressLine())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .driverName(entity.getDriver() != null
                        ? entity.getDriver().getUserName() + " " + entity.getDriver().getUserSurname()
                        : null)
                .status(entity.getStatus().name())
                .note(entity.getNote())
                .deliveredFeedback(entity.getDeliveredFeedback())
                .undeliveredFeedback(entity.getUndeliveredFeedback())
                .photoUrls(entity.getDocumentUrls())
                .createdAt(entity.getCreatedAt())
                .completedAt(entity.getCompletedAt())
                .failedAt(entity.getFailedAt())
                .build();
    }
}
