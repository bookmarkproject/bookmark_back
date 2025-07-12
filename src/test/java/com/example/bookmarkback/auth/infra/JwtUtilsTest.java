package com.example.bookmarkback.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookmarkback.global.exception.UnauthorizedException;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
@SpringBootTest
class JwtUtilsTest {

    @Autowired
    @Qualifier("loginJwtUtils")
    JwtUtils jwtUtils;
    @Autowired
    MemberRepository memberRepository;

    @Value(value = "${jwt.secret-key}")
    private String secretKey;
    @Value(value = "${application.name}")
    private String issuer;
    @Value(value = "${jwt.access-expiration}")
    private Long expirationTime;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Jwt 토큰을 생성할 수 있다.")
    void createTokenTest() {
        Member savedMember = memberRepository.save(getTestMember());
        String accessToken = jwtUtils.createAccessToken(savedMember);

        Claims claims = testParsePayload(accessToken);

        assertThat(Long.valueOf(claims.get(JwtUtils.JWT_MEMBER_ID_KEY, String.class))).isEqualTo(savedMember.getId());
        assertThat(claims.getIssuer()).isEqualTo(issuer);
    }

    @Test
    @DisplayName("인증헤더에서 토큰을 뽑아낼 수 있다.")
    void extractTokenTest() {
        Member savedMember = memberRepository.save(getTestMember());
        String accessToken = jwtUtils.createAccessToken(savedMember);
        String authorizationHeader = "Bearer " + accessToken;

        String extractedToken = jwtUtils.extractToken(authorizationHeader);

        assertThat(accessToken).isEqualTo(extractedToken);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    @DisplayName("인증헤더가 비어있으면 예외가 발생한다.")
    void extractTokenNullOrBlankTest(String authorizationHeader) {
        assertThatThrownBy(() -> jwtUtils.extractToken(authorizationHeader))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증 헤더가 비어있습니다.");
    }

    @Test
    @DisplayName("인증헤더가 Bearer로 시작하지 않으면 예외가 발생한다.")
    void extractTokenNotStartBearerTest() {
        Member savedMember = memberRepository.save(getTestMember());
        String accessToken = jwtUtils.createAccessToken(savedMember);

        assertThatThrownBy(() -> jwtUtils.extractToken(accessToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증 헤더의 prefix가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("Jwt에서 멤버 아이디와 역할을 뽑아낼 수 있다.")
    void accessTokenExtractTest() {
        Member savedMember = memberRepository.save(getTestMember());
        String accessToken = jwtUtils.createAccessToken(savedMember);

        Map<String, Object> memberIdAndRole = jwtUtils.extractMemberIdAndRole(accessToken);

        assertThat(savedMember.getId()).isEqualTo(memberIdAndRole.get(JwtUtils.JWT_MEMBER_ID_KEY));
        assertThat(savedMember.getRole().getName()).isEqualTo(memberIdAndRole.get(JwtUtils.JWT_ROLE_KEY));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    @DisplayName("Jwt 토큰이 비어있거나 null이면 예외가 발생한다.")
    void accessTokenNullOrBlankTest(String accessToken) {
        assertThatThrownBy(() -> jwtUtils.extractMemberIdAndRole(accessToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("JWT 토큰이 비어있습니다.");
    }

    private Member getTestMember() {
        return new Member("kkk@gmail.com", "xptmxm!@#123", "김철수", "포포뇽", "남자", "01012345678",
                LocalDate.of(1900, 12, 21), null, null);
    }

    private Claims testParsePayload(String jwtToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private String customCreateToken(Member member, String iss) {
        Date now = new Date();

        return Jwts.builder()
                .claim(JwtUtils.JWT_MEMBER_ID_KEY, member.getId())
                .issuer(iss)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .claim(JwtUtils.JWT_ROLE_KEY, member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}