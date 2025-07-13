package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.auth.validator.SignupValidator;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
        String password,

        String token
) {
    public ChangePasswordRequest {
        SignupValidator.isValidPassword(password);
    }
}
