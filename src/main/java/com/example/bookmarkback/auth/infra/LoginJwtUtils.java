package com.example.bookmarkback.auth.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginJwtUtils extends JwtUtils {

    @Value("${jwt.access.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
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
