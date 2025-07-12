package com.example.bookmarkback.auth.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PasswordChangeJwtUtils extends JwtUtils {
    @Value("${jwt.password.secret.key}")
    private String secretKey;

    @Value("${jwt.password.expiration}")
    private long expiration;

    @Value(value = "${application.name}")
    private String issuer;

    @Override
    protected String getSecretKey() {
        return this.secretKey;
    }

    @Override
    protected long getExpirationMillis() {
        return this.expiration;
    }

    @Override
    protected String getIssuer() {
        return this.issuer;
    }
}
