package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record SignupRequest(
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
        @NotBlank(message = "전화번호는 필수 항목입니다.")
        String phoneNumber,
        @NotBlank(message = "생년월일은 필수 항목입니다.")
        LocalDate birthday,
        String profileImage
) {
    public Member toMember() {
        return new Member(email, password, name, nickname, gender, phoneNumber, birthday, profileImage);
    }
}
