package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.ChangePasswordRequest;
import com.example.bookmarkback.auth.dto.FindEmailRequest;
import com.example.bookmarkback.auth.dto.LoginRequest;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.infra.JwtUtils;
import com.example.bookmarkback.auth.infra.PasswordChangeJwtUtils;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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

    @Autowired
    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                       EmailVerificationRepository emailVerificationRepository,
                       @Qualifier("loginJwtUtils") JwtUtils jwtUtils, PasswordChangeJwtUtils passwordChangeJwtUtils) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationRepository = emailVerificationRepository;
        this.jwtUtils = jwtUtils;
        this.passwordChangeJwtUtils = passwordChangeJwtUtils;
    }

    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) throws Exception {
        log.info("회원가입 시작");

        Member member = signupRequest.toMember(encodePassword(signupRequest.password()));
        checkDuplicationEmail(member.getEmail());
        checkDuplicationNickname(member.getNickname());

        checkEmailVerification(member.getEmail());

        try {
            Member savedMember = memberRepository.save(member);
            log.info("회원가입 성공 회원 id : {}", savedMember.getId());
            log.info("회원가입 성공 비밀번호 : {}", savedMember.getPassword());
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

        return MemberResponse.response(foundMember, jwtUtils.createAccessToken(foundMember));
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
        return true;
    }

    private Long extractToken(String token) {
        String extractedToken = passwordChangeJwtUtils.extractToken(token);
        Long memberId = (Long) passwordChangeJwtUtils.extractMemberIdAndRole(extractedToken)
                .get(JwtUtils.JWT_MEMBER_ID_KEY);
        return Long.valueOf(memberId);
    }

    private void deleteEmailVerification(String email) {
        emailVerificationRepository.deleteByEmail(email);
        log.info("{}에 해당하는 인증 정보 삭제", email);
    }

    private void checkEmailVerification(String email) {
        EmailVerification foundEmailVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                email).orElseThrow(() -> new BadRequestException("인증 내역이 존재하지 않습니다."));
        log.info("회원 가입 이메일 인증 여부 체크");
        log.info("회원 가입 인증 여부 : {}", foundEmailVerification.isVerified());
        log.info("회원 가입 유효 시간 여부 : {}", foundEmailVerification.getVerifiedAt());
        log.info("현재 시간 : {} ", LocalDateTime.now());
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
            log.error("중복 이메일 예외 발생");
            throw new BadRequestException("이미 가입된 이메일이 있습니다.");
        }
    }

    private void checkDuplicationNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            log.error("중복 닉네임 예외 발생");
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        }
    }

}
