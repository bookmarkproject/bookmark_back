package com.example.bookmarkback.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailResponse(
        boolean isVerified,
        String passwordChangeToken
) {

    public static EmailResponse response(boolean isVerified) {
        return EmailResponse.builder()
                .isVerified(isVerified)
                .build();
    }

    public static EmailResponse response(boolean isVerified, String token) {
        return EmailResponse.builder()
                .isVerified(isVerified)
                .passwordChangeToken(token)
                .build();
    }
}
