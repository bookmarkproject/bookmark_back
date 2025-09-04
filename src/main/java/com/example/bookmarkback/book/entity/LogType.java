package com.example.bookmarkback.book.entity;

import com.example.bookmarkback.global.exception.BadRequestException;
import java.util.Arrays;

public enum LogType {
    Normal("일반"),
    DONE("완독");

    private final String name;

    LogType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LogType toEnum(String name) {
        return Arrays.stream(values())
                .filter(questionType -> name.equals(questionType.name))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 유형이 존재하지 않습니다."));
    }
}
