package com.gabil.kargo.delivery.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.gabil.kargo.delivery.Delivery;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryDetailDto {

    private UUID id;
    private Long deliveryNo;

    private String recipientName;
    private String recipientPhone;
    private String addressLine;
    private Double latitude;
    private Double longitude;

    private String driverName;
    private String status;

    private String note;
    private String deliveredFeedback;
    private String undeliveredFeedback;

    private List<String> photoUrls;

    private Instant createdAt;
    private Instant completedAt;
    private Instant failedAt;
    
    
    
    public static DeliveryDetailDto of(Delivery d) {
        return DeliveryDetailDto.builder()
                .id(d.getId())
                .deliveryNo(d.getDeliveryNo())
                .recipientName(d.getRecipientName())
                .recipientPhone(d.getRecipientPhone())
                .addressLine(d.getAddressLine())
                .latitude(d.getLatitude())
                .longitude(d.getLongitude())
                .driverName(
                        d.getDriver() != null
                                ? d.getDriver().getUserName() + " " + d.getDriver().getUserSurname()
                                : null
                )
                .status(d.getStatus().name())
                .note(d.getNote())
                .deliveredFeedback(d.getDeliveredFeedback())
                .undeliveredFeedback(d.getUndeliveredFeedback())
                .photoUrls(d.getDocumentUrls())
                .createdAt(d.getCreatedAt())
                .completedAt(d.getCompletedAt())
                .failedAt(d.getFailedAt())
                .build();
    }

    
}
