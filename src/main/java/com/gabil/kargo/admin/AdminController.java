package com.gabil.kargo.admin;

import com.gabil.kargo.delivery.dto.*;
import com.gabil.kargo.driver.CreateDriverDTO;
import com.gabil.kargo.driver.DriverResponseDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gabil.kargo.delivery.Delivery;
import com.gabil.kargo.delivery.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final DeliveryService deliveryService;
    private final AdminService adminService;

    // 1) GET DELIVERY DETAIL
    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryDetailDto> getDeliveryDetail(@PathVariable UUID deliveryId) {
        DeliveryDetailDto dto = deliveryService.getDeliveryDetail(deliveryId);
        return ResponseEntity.ok(dto);
    }

    // 2) LIST ALL DRIVERS
    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        return ResponseEntity.ok(adminService.getAllDrivers());
    }

    // 3) CREATE DELIVERY
    @PostMapping("/create/delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryDetailDto> createDelivery(@RequestBody CreateDeliveryDTO dto) {

        Delivery delivery = deliveryService.createDelivery(dto);

        DeliveryDetailDto response = deliveryService.getDeliveryDetail(delivery.getId());

        return ResponseEntity.ok(response);
    }

    // 4) LIST ALL DELIVERIES (FILTERS)
    @GetMapping("/list/delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryDto> getDeliveries(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) UUID driverId,
            @RequestParam(required = false) String status
    ) {
        return deliveryService.getAllDeliveries(date, driverId, status);
    }
    
    
    // 5) DELETE DRIVER
    @DeleteMapping("/delete/driver/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id){
    	 	adminService.deleteDriver(id);
    	    return ResponseEntity.noContent().build(); // 204 No Content    	
    }
    
    
    
}
