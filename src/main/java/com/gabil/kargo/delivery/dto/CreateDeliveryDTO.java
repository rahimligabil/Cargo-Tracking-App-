package com.gabil.kargo.delivery.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class CreateDeliveryDTO {

    private String recipientName;
    private String recipientPhone;

    private String addressLine;
    private Double latitude;
    private Double longitude;

    private String note;

    private UUID driverId;
}
