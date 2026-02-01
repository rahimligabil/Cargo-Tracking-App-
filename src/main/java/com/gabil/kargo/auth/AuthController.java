package com.gabil.kargo.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabil.kargo.auth.dto.LoginRequest;
import com.gabil.kargo.auth.dto.LoginResponse;
import com.gabil.kargo.auth.dto.SignupRequest;
import com.gabil.kargo.user.User;
import com.gabil.kargo.user.dto.UserSummaryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<UserSummaryResponse> signUp(@RequestBody @Valid SignupRequest request) {

	    User user = authService.signup(request);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(UserSummaryResponse.of(user));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest login){

		LoginResponse response = authService.login(login);

		return ResponseEntity.status(HttpStatus.OK).body(response);

	}
}
