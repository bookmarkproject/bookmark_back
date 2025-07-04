package com.example.bookmarkback.auth.entity;

import com.example.bookmarkback.global.exception.BadRequestException;
import java.util.Arrays;

public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public static Gender toEnum(String name) {
        return Arrays.stream(values())
                .filter(gender -> name.equals(gender.name))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 성별이 존재하지 않습니다."));
    }
}
