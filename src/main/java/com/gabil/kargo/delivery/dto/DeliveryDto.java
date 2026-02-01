package com.gabil.kargo.delivery.dto;

import java.time.Instant;
import java.util.UUID;

import com.gabil.kargo.delivery.Delivery;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryDto {

    private UUID id;
    private Long deliveryNo;

    private String recipientName;
    private String addressLine;

    private String driverName;

    private String status;

    private Instant createdAt;
    
    
    public static DeliveryDto of(Delivery d) {
        return DeliveryDto.builder()
                .id(d.getId())
                .deliveryNo(d.getDeliveryNo())
                .recipientName(d.getRecipientName())
                .addressLine(d.getAddressLine())
                .driverName(
                        d.getDriver() != null
                                ? d.getDriver().getUserName() + " " + d.getDriver().getUserSurname()
                                : null
                )
                .status(d.getStatus().name())
                .createdAt(d.getCreatedAt())
                .build();
    }

}
