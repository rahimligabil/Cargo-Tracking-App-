package com.gabil.kargo.delivery.dto;

import com.gabil.kargo.delivery.Delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCustomerInfoResponse {

    private String name;
    private String address;
    private String phone;

    public static DeliveryCustomerInfoResponse of(Delivery delivery) {
        return new DeliveryCustomerInfoResponse(
                delivery.getRecipientName(),
                delivery.getAddressLine(),
                delivery.getRecipientPhone()
        );
    }
}
