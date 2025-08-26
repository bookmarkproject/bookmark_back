package com.example.bookmarkback.global.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RestExceptionHandlerTest {

    @Test
    @DisplayName("예외 발생시켜 보기")
    public void tryException() {
        Assertions.assertThatThrownBy(() -> throwException())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("예외 발생 했음.");
    }

    private String throwException() {
        throw new BadRequestException("예외 발생 했음.");
    }
}