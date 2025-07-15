package com.example.bookmarkback.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        @NotBlank(message = "refreshToken 값은 필수입니다.")
        String refreshToken
) {
}
