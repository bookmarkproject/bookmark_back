package com.example.bookmarkback.auth.dto;

import lombok.Builder;

@Builder
public record EmailResponse(
        boolean isVerified
) {

    public static EmailResponse response(boolean isVerified) {
        return EmailResponse.builder()
                .isVerified(isVerified)
                .build();
    }
}
