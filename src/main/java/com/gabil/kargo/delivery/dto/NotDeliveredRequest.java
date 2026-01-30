package com.gabil.kargo.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotDeliveredRequest {

    @NotBlank
    private String reason;

    private String note;

    private String photoUrl;
}
