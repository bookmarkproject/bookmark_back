package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.auth.validator.SignupValidator;
import com.example.bookmarkback.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
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
        @NotNull(message = "생년월일은 필수 항목입니다.")
        LocalDate birthday
) {

    public SignupRequest {
        SignupValidator.isValidPassword(password);
        SignupValidator.isValidPhoneNumber(phoneNumber);
    }

    public Member toMember(String encodedPassword) {
        return new Member(email, encodedPassword, name, nickname, gender, phoneNumber, birthday, null);
    }
}
