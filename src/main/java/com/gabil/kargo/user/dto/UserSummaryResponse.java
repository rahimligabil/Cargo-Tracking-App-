package com.gabil.kargo.user.dto;

import com.gabil.kargo.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String role;
    private boolean active;
    private Instant createdAt;
    private Instant lastLoginAt;

    public static UserSummaryResponse of(User user) {
        return new UserSummaryResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserSurname(),
                user.getUserEmail(),
                user.getPhone(),
                user.getRole().getRoleName(),
                user.isActive(),
                user.getCreatedAt(),
                user.getLastloginAt()
        );
    }
}
