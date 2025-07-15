package com.example.bookmarkback.member.service;

import com.example.bookmarkback.auth.repository.RefreshTokenRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public MemberResponse getMyInfo(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new BadRequestException("해당 id의 계정이 존재하지 않습니다."));
        return MemberResponse.response(member);
    }

    @Transactional
    public void logout(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new BadRequestException("해당하는 계정이 없습니다."));
        refreshTokenRepository.deleteByMember(member);
    }
}
