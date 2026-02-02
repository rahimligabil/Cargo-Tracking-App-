package com.gabil.kargo.delivery;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.gabil.kargo.delivery.dto.*;
import com.gabil.kargo.user.User;
import com.gabil.kargo.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    // ==========================================================
    //                     ADMIN METHODS
    // ==========================================================

    @Transactional
    public Delivery createDelivery(CreateDeliveryDTO dto) {

        User driver = userRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Sürücü bulunamadı"));

        long deliveryNo = nextDeliveryNo();

        Delivery delivery = Delivery.builder()
                .deliveryNo(deliveryNo)
                .recipientName(dto.getRecipientName())
                .recipientPhone(dto.getRecipientPhone())
                .addressLine(dto.getAddressLine())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .note(dto.getNote())
                .status(DeliveryStatus.PENDING)
                .driver(driver)
                .build();

        deliveryRepository.save(delivery);
        System.out.println("HELLO PROD");

        log.info("Yeni teslimat oluşturuldu → No: {} | Driver: {}", 
                delivery.getDeliveryNo(),
                driver.getUserName() + " " + driver.getUserSurname());

        return delivery;
    }


    @Transactional(readOnly = true)
    public List<DeliveryDto> getAllDeliveries(String date, UUID driverId, String status) {

        return deliveryRepository.findWithFilters(date, driverId, status)
                .stream()
                .map(DeliveryDto::of)
                .toList();
    }


    @Transactional(readOnly = true)
    public DeliveryDetailDto getDeliveryDetail(UUID id) {

        Delivery d = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teslimat bulunamadı"));

        return DeliveryDetailDto.of(d);
    }

    // ==========================================================
    //                     MOBILE METHODS
    // ==========================================================

    @Transactional(readOnly = true)
    public List<DeliveryListItemResponse> listDeliveriesForDriver(UUID driverId, Set<DeliveryStatus> statuses) {

        Set<DeliveryStatus> filters = 
                (statuses == null || statuses.isEmpty()) 
                        ? Set.of(DeliveryStatus.PENDING) 
                        : statuses;

        List<Delivery> deliveries = deliveryRepository.findByDriverUserIdAndStatusInOrderByCreatedAtDesc(
                driverId,
                List.copyOf(filters)
        );

        return deliveries.stream()
                .map(DeliveryListItemResponse::of)
                .toList();
    }


    @Transactional(readOnly = true)
    public DeliveryDetailResponse getDeliveryForDriver(UUID id, UUID driverId) {
        Delivery delivery = loadDeliveryOrThrow(id, driverId);
        return DeliveryDetailResponse.of(delivery);
    }


    @Transactional
    public DeliveryDetailResponse markAsCompleted(UUID id, UUID driverId) {

        Delivery delivery = loadDeliveryOrThrow(id, driverId);

        if (delivery.getStatus() != DeliveryStatus.COMPLETED) {
            delivery.setStatus(DeliveryStatus.COMPLETED);
            delivery.setCompletedAt(Instant.now());
        }

        delivery = deliveryRepository.save(delivery);
        return DeliveryDetailResponse.of(delivery);
    }


    @Transactional
    public DeliveryDetailResponse markAsCompleted(UUID id, UUID driverId, MultipartFile proof, String feedback) {

        Delivery delivery = loadDeliveryOrThrow(id, driverId);

        delivery.setStatus(DeliveryStatus.COMPLETED);
        delivery.setCompletedAt(Instant.now());

        if (StringUtils.hasText(feedback)) {
            delivery.setDeliveredFeedback(feedback);
        }

        delivery.setUndeliveredFeedback(null);
        delivery.setFailedAt(null);

        deliveryRepository.save(delivery);

        return DeliveryDetailResponse.of(delivery);
    }


    @Transactional
    public DeliveryDetailResponse markAsNotDelivered(UUID id, UUID driverId, NotDeliveredRequest request) {

        Delivery delivery = loadDeliveryOrThrow(id, driverId);

        delivery.setStatus(DeliveryStatus.NOT_DELIVERED);
        delivery.setFailedAt(Instant.now());
        delivery.setCompletedAt(null);
        delivery.setDeliveredFeedback(null);
        delivery.setUndeliveredFeedback(buildUndeliveredFeedback(request));

        deliveryRepository.save(delivery);

        return DeliveryDetailResponse.of(delivery);
    }


    // ==========================================================
    //                      HELPER METHODS
    // ==========================================================

    private Delivery loadDeliveryOrThrow(UUID id, UUID driverId) {
        return deliveryRepository.findByIdAndDriverUserId(id, driverId)
                .orElseThrow(() -> 
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found for driver: " + id));
    }


    private String buildUndeliveredFeedback(NotDeliveredRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reason: ").append(req.getReason());

        if (StringUtils.hasText(req.getNote())) {
            sb.append(" | Note: ").append(req.getNote());
        }
        if (StringUtils.hasText(req.getPhotoUrl())) {
            sb.append(" | Photo: ").append(req.getPhotoUrl());
        }
        return sb.toString();
    }

    private long nextDeliveryNo() {
        Long max = deliveryRepository.findMaxDeliveryNo();
        return (max == null ? 0L : max) + 1L;
    }
}
