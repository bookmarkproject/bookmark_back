package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.ChangePasswordRequest;
import com.example.bookmarkback.auth.dto.FindEmailRequest;
import com.example.bookmarkback.auth.dto.LoginRequest;
import com.example.bookmarkback.auth.dto.RefreshTokenRequest;
import com.example.bookmarkback.auth.dto.RefreshTokenResponse;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.entity.RefreshToken;
import com.example.bookmarkback.auth.infra.JwtUtils;
import com.example.bookmarkback.auth.infra.PasswordChangeJwtUtils;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.auth.repository.RefreshTokenRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import jakarta.validation.Valid;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JwtUtils jwtUtils;
    private final PasswordChangeJwtUtils passwordChangeJwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final Long REFRESH_TOKEN_DURATION = 14L;

    @Autowired
    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                       EmailVerificationRepository emailVerificationRepository,
                       @Qualifier("loginJwtUtils") JwtUtils jwtUtils, PasswordChangeJwtUtils passwordChangeJwtUtils,
                       RefreshTokenRepository refreshTokenRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationRepository = emailVerificationRepository;
        this.jwtUtils = jwtUtils;
        this.passwordChangeJwtUtils = passwordChangeJwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) throws Exception {
        Member member = signupRequest.toMember(encodePassword(signupRequest.password()));
        checkDuplicationEmail(member.getEmail());
        checkDuplicationNickname(member.getNickname());

        checkEmailVerification(member.getEmail());

        try {
            Member savedMember = memberRepository.save(member);
            deleteEmailVerification(savedMember.getEmail());
            return MemberResponse.response(savedMember);
        } catch (Exception e) {
            log.error("서버 예외 발생");
            throw new Exception(e);
        }
    }

    @Transactional
    public MemberResponse login(LoginRequest loginRequest) {
        Member foundMember = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadRequestException("존재하지 않는 이메일입니다."));
        if (!matchPassword(loginRequest.password(), foundMember.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        foundMember.setLastLoginAt(LocalDateTime.now());
        String refreshToken = generatedRefreshToken();
        RefreshToken foundToken = refreshTokenRepository.findByMember(foundMember).orElse(null);
        if (foundToken != null) {
            foundToken.setToken(refreshToken);
            foundToken.setExpiredAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_DURATION));
        } else {
            RefreshToken savedToken = refreshTokenRepository.save(
                    new RefreshToken(foundMember, refreshToken,
                            LocalDateTime.now().plusDays(REFRESH_TOKEN_DURATION)));
        }

        return MemberResponse.response(foundMember, jwtUtils.createAccessToken(foundMember), refreshToken);
    }

    @Transactional(readOnly = true)
    public MemberResponse findEmail(FindEmailRequest findEmailRequest) {
        Member foundMember = memberRepository.findByNameAndPhoneNumber(findEmailRequest.name(),
                        findEmailRequest.phoneNumber())
                .orElseThrow(() -> new BadRequestException("해당 정보를 가진 계정이 존재하지 않습니다."));

        return MemberResponse.response(foundMember.getEmail());
    }

    @Transactional
    public void changePassword(@Valid ChangePasswordRequest changePasswordRequest) {
        Long memberId = extractToken(changePasswordRequest.token());
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("해당 토큰에 해당하는 계정이 존재하지 않습니다."));

        checkEmailVerification(foundMember.getEmail());

        foundMember.setPassword(encodePassword(changePasswordRequest.password()));
        deleteEmailVerification(foundMember.getEmail());
    }

    public Boolean checkNicknameDuplication(String nickname) {
        checkDuplicationNickname(nickname);
        return false;
    }

    @Transactional
    public RefreshTokenResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        RefreshToken foundToken = refreshTokenRepository.findByToken(refreshToken).
                orElseThrow(() -> new BadRequestException("RefreshToken이 존재하지 않습니다."));
        if (foundToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(foundToken);
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }

        String newRefreshToken = generatedRefreshToken();
        foundToken.setToken(newRefreshToken);
        foundToken.setExpiredAt(LocalDateTime.now().plusDays(14));

        return RefreshTokenResponse.response(jwtUtils.createAccessToken(foundToken.getMember()), newRefreshToken);
    }

    private String generatedRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32]; // 256비트
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Long extractToken(String token) {
        String extractedToken = passwordChangeJwtUtils.extractToken(token);
        Long memberId = (Long) passwordChangeJwtUtils.extractMemberIdAndRole(extractedToken)
                .get(JwtUtils.JWT_MEMBER_ID_KEY);
        return Long.valueOf(memberId);
    }

    private void deleteEmailVerification(String email) {
        emailVerificationRepository.deleteByEmail(email);
    }

    private void checkEmailVerification(String email) {
        EmailVerification foundEmailVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                email).orElseThrow(() -> new BadRequestException("인증 내역이 존재하지 않습니다."));
        if (!foundEmailVerification.isVerified()) {
            throw new BadRequestException("이메일 인증을 진행하지 않은 사용자입니다.");
        } else if (foundEmailVerification.getVerifiedAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("이메일 인증 유효 시간이 초과되었습니다.");
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean matchPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void checkDuplicationEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BadRequestException("이미 가입된 이메일이 있습니다.");
        }
    }

    private void checkDuplicationNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        }
    }
}
