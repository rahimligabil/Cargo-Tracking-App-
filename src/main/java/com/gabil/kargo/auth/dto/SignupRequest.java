package com.gabil.kargo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Email
    private String email; 

    private String phone;
}
