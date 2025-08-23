package com.example.bookmarkback.member.service;

import com.example.bookmarkback.auth.repository.RefreshTokenRepository;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookLogQuestionRepository;
import com.example.bookmarkback.book.repository.BookLogRepository;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final BookRecordRepository bookRecordRepository;
    private final BookLogRepository bookLogRepository;
    private final BookLogQuestionRepository bookLogQuestionRepository;

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

    @Transactional
    public void deleteMember(Long id, MemberAuth memberAuth) {
        validateRequest(id, memberAuth);
        deleteProcess(id);
    }

    private void deleteProcess(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("계정 정보가 존재하지 않습니다."));
        refreshTokenRepository.deleteByMember(member);

        List<BookRecord> bookRecords = bookRecordRepository.findByMemberId(memberId);
        for (BookRecord bookRecord : bookRecords) {
            List<BookLog> bookLogs = bookLogRepository.findByBookRecord(bookRecord);
            for (BookLog bookLog : bookLogs) {
                List<BookLogQuestion> bookLogQuestions = bookLogQuestionRepository.findByBookLogId(bookLog.getId());
                for (BookLogQuestion bookLogQuestion : bookLogQuestions) {
                    bookLogQuestionRepository.delete(bookLogQuestion);
                }
                bookLogRepository.delete(bookLog);
            }
            bookRecordRepository.delete(bookRecord);
        }

        memberRepository.delete(member);
    }

    private void validateRequest(Long id, MemberAuth memberAuth) {
        if (!id.equals(memberAuth.memberId())) {
            throw new ForbiddenException("다른 사용자의 계정을 삭제할 수 없습니다.");
        }
    }


}
