package com.gabil.kargo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

	@NotBlank
	@Size(max = 100)
	private String name;

	@NotBlank
	@Size(max = 100)
	private String surname;

	@NotBlank
	@Email
	@Size(max = 256)
	private String email;

	@NotBlank
	@Size(max = 32)
	private String phone;
}
