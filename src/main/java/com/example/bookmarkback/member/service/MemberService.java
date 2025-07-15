package com.example.bookmarkback.member.service;

import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMyInfo(MemberAuth memberAuth) {
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new BadRequestException("해당 id의 계정이 존재하지 않습니다."));
        return MemberResponse.response(member);
    }
}
