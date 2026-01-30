package com.gabil.kargo.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabil.kargo.driver.CreateDriverDTO;
import com.gabil.kargo.driver.DriverResponseDTO;
import com.gabil.kargo.user.User;
import com.gabil.kargo.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;

    public List<DriverResponseDTO> getAllDrivers() {
        List<User> drivers = userRepository.findAllDriversActive();

        return drivers.stream()
                .map(user -> DriverResponseDTO.builder()
                        .id(user.getUserId())
                        .firstName(user.getUserName())
                        .lastName(user.getUserSurname())
                        .email(user.getUserEmail())
                        .phone(user.getPhone())
                        .status(user.isActive() ? "AKTIF" : "PASIF")
                        .build()
                )
                .toList();
    }

	public void deleteDriver(UUID id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		
		user.setDeleted(true);
		user.setActive(false);
		
		userRepository.save(user);
		
	}
	
	
}
