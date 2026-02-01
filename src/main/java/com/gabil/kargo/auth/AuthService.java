package com.gabil.kargo.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabil.kargo.auth.dto.LoginRequest;
import com.gabil.kargo.auth.dto.LoginResponse;
import com.gabil.kargo.auth.dto.SignupRequest;
import com.gabil.kargo.user.User;
import com.gabil.kargo.user.UserService;
import com.gabil.kargo.user.dto.UserSummaryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest login) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			throw new IllegalStateException("Authentication is required for login.");
		}

		Object principal = authentication.getPrincipal();

		if (!(principal instanceof User currentUser)) {
			throw new IllegalStateException("Unexpected authentication principal type: " + principal.getClass());
		}

		User user = userService.loadOrCreateUser(
				currentUser.getFirebaseUid(),
				login.getEmail(),
				currentUser.getUserName()
		);

		boolean incomplete = user.getUserSurname() == null
				|| user.getUserSurname().isBlank()
				|| user.getPhone() == null
				|| user.getPhone().isBlank();

		String roleName = user.getRole() != null ? user.getRole().getRoleName() : "ROLE_DRIVER";

		return new LoginResponse(UserSummaryResponse.of(user), incomplete, roleName);
	}

	public User signup(SignupRequest req) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			throw new IllegalStateException("Authentication is required for signup.");
		}

		Object principal = authentication.getPrincipal();

		if (!(principal instanceof User authenticatedUser)) {
			throw new IllegalStateException("Unexpected authentication principal type: " + principal.getClass());
		}

		return userService.createUserFromSignup(
				authenticatedUser.getFirebaseUid(),
				req.getEmail(),
				req.getName(),
				req.getSurname(),
				req.getPhone()
		);
	}
}
