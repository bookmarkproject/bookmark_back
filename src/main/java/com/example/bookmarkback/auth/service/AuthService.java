package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) throws Exception {
        log.info("회원가입 시작");

        Member member = signupRequest.toMember(encodePassword(signupRequest.password()));
        checkDuplicationEmail(member.getEmail());
        checkDuplicationNickname(member.getNickname());

        try {
            Member savedMember = memberRepository.save(member);
            log.info("회원가입 성공 회원 아이디 : {}", savedMember.getId());
            return MemberResponse.response(savedMember);
        } catch (Exception e) {
            log.error("서버 예외 발생");
            throw new Exception(e);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
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
