package com.example.bookmarkback.member.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MemberRequest(
        @NotBlank(message = "이메일은 필수 항목입니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        String password,
        @NotBlank(message = "이름은 필수 항목입니다.")
        String name,
        @NotBlank(message = "닉네임은 필수 항목입니다.")
        String nickname,
        @NotBlank(message = "성별은 필수 항목입니다.")
        String gender,
        @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
        String phoneNumber,
        @NotBlank(message = "생년월일은 필수 항목입니다.")
        LocalDate birthday,
        String profileImage
) {
}
