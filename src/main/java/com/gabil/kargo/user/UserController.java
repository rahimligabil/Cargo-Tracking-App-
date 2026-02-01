package com.gabil.kargo.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabil.kargo.user.dto.UserProfileUpdateRequest;
import com.gabil.kargo.user.dto.UserSummaryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/get/me")
	public UserSummaryResponse getProfile(@AuthenticationPrincipal User authenticatedUser) {
		return userService.getUserProfile(authenticatedUser.getUserId());
	}

	@PutMapping("/update/me")
	public UserSummaryResponse updateProfile(@AuthenticationPrincipal User authenticatedUser,
			@RequestBody @Valid UserProfileUpdateRequest request) {
		return userService.updateUserProfile(authenticatedUser.getUserId(), request);
	}
}
