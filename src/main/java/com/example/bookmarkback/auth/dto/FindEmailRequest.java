package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.auth.validator.SignupValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record FindEmailRequest(
        @NotBlank(message = "이름은 필수 항목입니다.")
        String name,

        @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
        @Pattern(
                regexp = "^01[016789]\\d{7,8}$",
                message = "유효하지 않은 휴대폰 번호입니다."
        )
        String phoneNumber
) {

}
