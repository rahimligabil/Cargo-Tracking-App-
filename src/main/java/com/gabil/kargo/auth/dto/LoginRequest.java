package com.gabil.kargo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

	@Email
	@NotBlank
	@Size(max = 256)
	private String email;
}
