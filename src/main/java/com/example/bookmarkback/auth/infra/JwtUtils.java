package com.example.bookmarkback.auth.infra;

import com.example.bookmarkback.member.entity.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final static String JWT_MEMBER_ID_KEY = "member_id";
    private final static String JWT_ROLE_KEY = "role";

    private final String issuer;
    private final Long expirationTime;
    private final SecretKey secretKey;

    public JwtUtils(
            @Value(value = "${application.name}")
            String issuer,
            @Value(value = "${jwt.access-expiration}")
            Long expirationTime,
            @Value(value = "${jwt.secret-key}")
            String secretKey
    ) {
        this.issuer = issuer;
        this.expirationTime = expirationTime;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(Member member) {
        Date now = new Date();

        return Jwts.builder()
                .claim(JWT_MEMBER_ID_KEY, member.getId())
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .claim(JWT_ROLE_KEY, member.getRole())
                .signWith(secretKey)
                .compact();
    }

}
