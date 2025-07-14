package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.auth.validator.SignupValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(
        
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,16}$",
                message = "비밀번호는 영어, 숫자, 특수문자(!@#$%^&*())를 최소 1개 이상 포함해야 합니다."
        )
        String password,

        String token
) {

}
