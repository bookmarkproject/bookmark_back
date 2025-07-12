package com.example.bookmarkback.auth.dto;

import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.entity.Gender;
import java.util.Arrays;

public enum AuthCheckType {
    SIGNUP("signup"),
    PASSWORDCHANGE("passwordChange");

    private final String type;

    AuthCheckType(String type) {
        this.type = type;
    }

    public static AuthCheckType toEnum(String type) {
        return Arrays.stream(values())
                .filter(authCheckType -> type.equals(authCheckType.type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 타입의 메일 인증이 존재하지 않습니다."));
    }
}
