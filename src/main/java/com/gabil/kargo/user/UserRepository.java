package com.gabil.kargo.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,UUID>{
	Optional<User> findByFirebaseUid(String uid);
    Optional<User> findByUserEmail(String email);
    boolean existsByUserEmail(String email);
    

    @Query("SELECT u FROM User u WHERE u.role.roleName = 'ROLE_DRIVER' AND u.isDeleted = false")
    List<User> findAllDriversActive();

}
