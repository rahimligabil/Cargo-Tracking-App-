package com.gabil.kargo.auth.dto;

import com.gabil.kargo.user.dto.UserSummaryResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private UserSummaryResponse user;
    private boolean profileIncomplete;
    private String role;

    public LoginResponse(UserSummaryResponse user, boolean profileIncomplete, String role) {
        this.user = user;
        this.profileIncomplete = profileIncomplete;
        this.role = role;
    }
}
