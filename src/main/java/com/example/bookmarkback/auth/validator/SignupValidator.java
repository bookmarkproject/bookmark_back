package com.example.bookmarkback.auth.validator;

import com.example.bookmarkback.global.exception.BadRequestException;

public class SignupValidator {

    public static void isValidPassword(String password) {
        if (!isValidPasswordBlankOrNull(password)) {
            throw new BadRequestException("비밀번호는 필수 항목입니다.");
        }
        if (!isValidPasswordLength(password)) {
            throw new BadRequestException("비밀번호는 8자 이상 16자 이하여야 합니다.");
        }
    }

    private static boolean isValidPasswordBlankOrNull(String password) {
        return password != null && !password.isBlank();
    }

    private static boolean isValidPasswordLength(String password) {
        if (password.length() < 8 || password.length() > 16) {
            return false;
        }
        return true;
    }
}
