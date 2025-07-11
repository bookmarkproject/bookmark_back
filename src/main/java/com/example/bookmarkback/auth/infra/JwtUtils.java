package com.example.bookmarkback.auth.infra;

import com.example.bookmarkback.global.exception.UnauthorizedException;
import com.example.bookmarkback.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final static String JWT_TOKEN_PREFIX = "Bearer ";

    public final static String JWT_MEMBER_ID_KEY = "member_id";
    public final static String JWT_ROLE_KEY = "role";

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
                .claim(JWT_MEMBER_ID_KEY, String.valueOf(member.getId()))
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .claim(JWT_ROLE_KEY, member.getRole())
                .signWith(secretKey)
                .compact();
    }

    public String extractToken(String authorizationHeader) {
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new UnauthorizedException("인증 헤더가 비어있습니다.");
        } else if (!authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
            throw new UnauthorizedException("인증 헤더의 prefix가 올바르지 않습니다.");
        }

        return authorizationHeader.split(" ")[1];
    }

    public Map<String, Object> extractMemberIdAndRole(String jwtToken) {
        try {
            return extract(jwtToken);
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("JWT 토큰이 비어있습니다.");
        } catch (ExpiredJwtException exception) {
            throw new UnauthorizedException("JWT 토큰이 만료되었습니다.");
        } catch (Exception exception) {
            throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
        }
    }

    private Map<String, Object> extract(String jwtToken) {
        Map<String, Object> data = new HashMap<>();

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        validateIssuer(claims.getIssuer());

        data.put(JWT_MEMBER_ID_KEY, Long.valueOf(claims.get(JWT_MEMBER_ID_KEY, String.class)));
        data.put(JWT_ROLE_KEY, claims.get(JWT_ROLE_KEY, String.class));

        return data;
    }

    private void validateIssuer(String issuer) {
        if (!issuer.equals(this.issuer)) {
            throw new UnauthorizedException("해당 서비스에서 발급 받은 토큰이 아닙니다.");
        }
    }
}
