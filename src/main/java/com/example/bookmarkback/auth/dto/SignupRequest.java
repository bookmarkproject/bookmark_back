package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record SignupRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,16}$",
                message = "비밀번호는 영어, 숫자, 특수문자(!@#$%^&*())를 최소 1개 이상 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수 항목입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        String nickname,

        @NotBlank(message = "성별은 필수 항목입니다.")
        @Pattern(regexp = "^(남자|여자)$", message = "성별은 '남자' 또는 '여자'만 입력 가능합니다.")
        String gender,

        @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
        @Pattern(
                regexp = "^01[016789]\\d{7,8}$",
                message = "유효하지 않은 휴대폰 번호 입니다."
        )
        String phoneNumber,
        @NotNull(message = "생년월일은 필수 항목입니다.")
        LocalDate birthday
) {

    public Member toMember(String encodedPassword) {
        return new Member(email, encodedPassword, name, nickname, gender, phoneNumber, birthday, null, null);
    }
}
