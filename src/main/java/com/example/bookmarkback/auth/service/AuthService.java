package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final MemberRepository memberRepository;

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) throws Exception {
        Member member = signupRequest.toMember();
        checkDuplicationEmail(member.getEmail());
        checkDuplicationNickname(member.getNickname());

        try {
            Member savedMember = memberRepository.save(member);
            return MemberResponse.response(savedMember);
        } catch (Exception e) {
            throw new Exception(e);
        }
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
