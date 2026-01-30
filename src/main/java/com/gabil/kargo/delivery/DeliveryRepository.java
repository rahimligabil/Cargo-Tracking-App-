package com.gabil.kargo.delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    // WEB ADMIN PANEL SORGUSU
    @Query("""
        SELECT d FROM Delivery d
        WHERE (:date IS NULL OR CAST(d.createdAt AS date) = CAST(:date AS date))
          AND (:driverId IS NULL OR d.driver.userId = :driverId)
          AND (:status IS NULL OR d.status = :status)
        ORDER BY d.createdAt DESC
    """)
    List<Delivery> findWithFilters(String date, UUID driverId, String status);


    
    // MOBİL SÜRÜCÜ SORGULARI
    List<Delivery> findByStatusOrderByCreatedAtDesc(DeliveryStatus status);

    List<Delivery> findByDriverUserIdAndStatusOrderByCreatedAtDesc(UUID driverId, DeliveryStatus status);

    List<Delivery> findByDriverUserIdAndStatusInOrderByCreatedAtDesc(UUID driverId, List<DeliveryStatus> statuses);

    Optional<Delivery> findByIdAndDriverUserId(UUID id, UUID driverId);

    @Query("select coalesce(max(d.deliveryNo), 0) from Delivery d")
    Long findMaxDeliveryNo();
}
