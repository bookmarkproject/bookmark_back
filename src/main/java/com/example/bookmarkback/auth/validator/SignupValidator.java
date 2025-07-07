package com.example.bookmarkback.auth.validator;

import com.example.bookmarkback.global.exception.BadRequestException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SignupValidator {

    public static void isValidPassword(String password) {
        if (!isValidPasswordBlankOrNull(password)) {
            throw new BadRequestException("비밀번호는 필수 항목입니다.");
        }
        if (!isValidPasswordAllowedChar(password)) {
            throw new BadRequestException("허용되지 않는 문자가 포함되어 있습니다.");
        }
        if (!isValidPasswordLength(password)) {
            throw new BadRequestException("비밀번호는 8자 이상 16자 이하여야 합니다.");
        }
        if (!isValidPasswordContainNumberAndLetter(password)) {
            throw new BadRequestException("비밀번호는 영어와 숫자가 최소 1개 이상 포함 되어야 합니다.");
        }
        if (!isValidPasswordContainSpecialChar(password)) {
            throw new BadRequestException("비밀번호는 특수문자!@#$%^&*() 중 최소 1개 이상을 포함해야 합니다.");
        }

    }

    public static void isValidPhoneNumber(String phoneNumber) {
        String regex = "^01[016789]\\d{7,8}$";
        if (!phoneNumber.matches(regex)) {
            throw new BadRequestException("유효하지 않은 휴대폰 번호입니다.");
        }
        ;
    }

    private static boolean isValidPasswordAllowedChar(String password) {
        Set<Character> allowedChars = new HashSet<>();
        initAllowedChar(allowedChars);

        for (char ch : password.toCharArray()) {
            if (!allowedChars.contains(ch)) {
                return false;
            }
        }
        return true;
    }

    private static void initAllowedChar(Set<Character> allowedChars) {
        for (char c = 'a'; c <= 'z'; c++) {
            allowedChars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            allowedChars.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            allowedChars.add(c);
        }

        char[] specials = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};
        for (char c : specials) {
            allowedChars.add(c);
        }
    }

    private static boolean isValidPasswordContainSpecialChar(String password) {
        Set<Character> specialChar = Set.of('!', '@', '#', '$', '%', '^', '&', '*', '(', ')');
        for (char ch : password.toCharArray()) {
            if (specialChar.contains(ch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidPasswordContainNumberAndLetter(String password) {
        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            if (hasLetter && hasNumber) {
                return true;
            }
        }

        return false;
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
