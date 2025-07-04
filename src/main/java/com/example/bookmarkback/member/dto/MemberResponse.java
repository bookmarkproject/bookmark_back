package com.example.bookmarkback.member.dto;

import com.example.bookmarkback.member.entity.Member;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MemberResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String gender,
        String role,
        LocalDate birthday,
        String profileImage
) {
    public static MemberResponse response(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .gender(member.getGender().getName())
                .role(member.getRole().getName())
                .birthday(member.getBirthday())
                .profileImage(member.getProfileImage())
                .build();
    }
}

