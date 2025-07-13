package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.auth.validator.SignupValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FindEmailRequest(
        @NotBlank(message = "이름은 필수 항목입니다.")
        String name,
        @NotBlank(message = "전화번호는 필수 항목입니다.")
        String phoneNumber
) {
    public FindEmailRequest {
        SignupValidator.isValidPhoneNumber(phoneNumber);
    }
}
