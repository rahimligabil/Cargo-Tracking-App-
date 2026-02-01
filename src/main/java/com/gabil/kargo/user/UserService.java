package com.gabil.kargo.user;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gabil.kargo.role.Role;
import com.gabil.kargo.role.RoleRepository;
import com.gabil.kargo.user.dto.UserProfileUpdateRequest;
import com.gabil.kargo.user.dto.UserSummaryResponse;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public User loadOrCreateUser(String uid, String email,@Nullable String name) {
		Instant now = Instant.now();

		return userRepository.findByFirebaseUid(uid)
				.map(user -> {
					user.setLastloginAt(now);

					if (user.getUserEmail() == null || user.getUserEmail().isBlank()) {
						user.setUserEmail(email);
					}

					if (name != null && !name.isBlank() && (user.getUserName() == null || user.getUserName().isBlank())) {
						user.setUserName(name);
					}

					if ((user.getUserSurname() == null || user.getUserSurname().isBlank()) && name != null && !name.isBlank()) {
						user.setUserSurname(name);
					}

					return userRepository.save(user);
				})
				.orElseGet(() -> {
					Role userRole = roleRepository.findByRoleName("ROLE_DRIVER")
							.orElseThrow(() -> new IllegalStateException("Role not found."));

					User user = new User();
					user.setFirebaseUid(uid);
					user.setUserEmail(email);
					user.setRole(userRole);
					user.setActive(true);
					user.setLastloginAt(now);

					if (name != null && !name.isBlank()) {
						user.setUserName(name);
						user.setUserSurname(name);
					} else {
						user.setUserName("");
						user.setUserSurname("");
					}

					return userRepository.save(user);
				});

	}

	public User createUserFromSignup(String firebaseUid, String userEmail, String name, String surname, String phone) {
		
		Instant now = Instant.now();

		Role userRole = roleRepository.findByRoleName("ROLE_DRIVER")
				.orElseThrow(() -> new IllegalStateException("Role not found: ROLE_USER"));

		User user = userRepository.findByFirebaseUid(firebaseUid)
				.orElseGet(() -> {
					User created = new User();
					created.setFirebaseUid(firebaseUid);
					created.setRole(userRole);
					created.setActive(true);
					return created;
			});

		user.setActive(true);
		user.setFirebaseUid(firebaseUid);
		user.setUserEmail(userEmail);
		user.setUserSurname(surname != null ? surname : "");
		user.setUserName(name != null ? name : "");
		user.setPhone(phone);
		user.setLastloginAt(now);

		if (user.getRole() == null) {
			user.setRole(userRole);
		}

		return userRepository.save(user);
	}

	public Optional<User> findByFirebaseUid(String firebaseUid) {
		return userRepository.findByFirebaseUid(firebaseUid);
	}

	@Transactional(readOnly = true)
	public UserSummaryResponse getUserProfile(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		return UserSummaryResponse.of(user);
	}

	@Transactional
	public UserSummaryResponse updateUserProfile(UUID userId, UserProfileUpdateRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if (StringUtils.hasText(request.getEmail())
				&& !request.getEmail().equalsIgnoreCase(user.getUserEmail())
				&& userRepository.existsByUserEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
		}

		user.setUserName(request.getName());
		user.setUserSurname(request.getSurname());
		user.setUserEmail(request.getEmail());
		user.setPhone(request.getPhone());

		User saved = userRepository.save(user);
		return UserSummaryResponse.of(saved);
	}

}
