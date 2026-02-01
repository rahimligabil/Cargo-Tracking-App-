package com.gabil.kargo.delivery;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gabil.kargo.delivery.dto.DeliveryDetailResponse;
import com.gabil.kargo.delivery.dto.DeliveryListItemResponse;
import com.gabil.kargo.delivery.dto.NotDeliveredRequest;
import com.gabil.kargo.user.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class MobilDeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public List<DeliveryListItemResponse> listDeliveries(
            @AuthenticationPrincipal User driver,
            @RequestParam(name = "status", required = false) String status) {
        Set<DeliveryStatus> statusFilter = resolveStatuses(status);
        return deliveryService.listDeliveriesForDriver(driver.getUserId(), statusFilter);
    }

    @GetMapping("/{id}")
    public DeliveryDetailResponse getDelivery(@AuthenticationPrincipal User driver, @PathVariable UUID id) {
        return deliveryService.getDeliveryForDriver(id, driver.getUserId());
    }

    @PostMapping(value = "/{id}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DeliveryDetailResponse completeDelivery(@AuthenticationPrincipal User driver,
                                                   @PathVariable UUID id,
                                                   @RequestPart(value = "proof", required = false) MultipartFile proof,
                                                   @RequestPart(value = "feedback", required = false) String feedback) {
        return deliveryService.markAsCompleted(id, driver.getUserId(), proof, feedback);
    }

    @PostMapping("/{id}/fail")
    public DeliveryDetailResponse failDelivery(@AuthenticationPrincipal User driver,
                                               @PathVariable UUID id,
                                               @RequestBody @Valid NotDeliveredRequest request) {
        return deliveryService.markAsNotDelivered(id, driver.getUserId(), request);
    }

    @PostMapping("/{id}/not-delivered")
    public DeliveryDetailResponse failDeliveryAlias(@AuthenticationPrincipal User driver,
                                                    @PathVariable UUID id,
                                                    @RequestBody @Valid NotDeliveredRequest request) {
        return deliveryService.markAsNotDelivered(id, driver.getUserId(), request);
    }

    private Set<DeliveryStatus> resolveStatuses(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of(DeliveryStatus.PENDING);
        }

        String normalizedInput = raw.trim();

        // Support comma-separated values like "COMPLETED,NOT_DELIVERED" or shortcuts like "DONE"
        Set<DeliveryStatus> statuses = Arrays.stream(normalizedInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(this::mapToStatus)
                .flatMap(Set::stream)
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        if (statuses.isEmpty()) {
            return Set.of(DeliveryStatus.PENDING);
        }

        return statuses;
    }

    private Set<DeliveryStatus> mapToStatus(String raw) {
        String normalized = raw.replace("-", "_").replace(" ", "_").toUpperCase();
        if ("NOTDELIVERED".equals(normalized)) {
            normalized = "NOT_DELIVERED";
        }
        if ("DONE".equals(normalized) || "COMPLETED_NOT_DELIVERED".equals(normalized)
                || "COMPLETED_OR_NOT_DELIVERED".equals(normalized)) {
            return Set.of(DeliveryStatus.COMPLETED, DeliveryStatus.NOT_DELIVERED);
        }
        try {
            return Set.of(DeliveryStatus.valueOf(normalized));
        } catch (IllegalArgumentException ex) {
            return Set.of();
        }
    }
}
