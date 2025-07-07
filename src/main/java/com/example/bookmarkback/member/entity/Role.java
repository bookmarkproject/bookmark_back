package com.example.bookmarkback.member.entity;

import com.example.bookmarkback.global.exception.BadRequestException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Role {
    ADMIN("관리자"),
    USER("유저");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role toEnum(String name) {
        return Arrays.stream(values())
                .filter((role) -> name.equals(role.name))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 이름의 권한이 존재하지 않습니다."));
    }


}
