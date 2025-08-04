package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookLogQuestionResponse;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookLogQuestionRepository;
import com.example.bookmarkback.book.repository.BookLogRepository;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookLogQuestionService {

    private final BookRecordRepository bookRecordRepository;
    private final BookLogRepository bookLogRepository;
    private final BookLogQuestionRepository bookLogQuestionRepository;

    @Transactional(readOnly = true)
    public List<BookLogQuestionResponse> getBookLogQuestions(Long bookLogId, MemberAuth memberAuth) {
        validateMemberAuth(bookLogId, memberAuth);
        return bookLogQuestionRepository.findByBookLogId(bookLogId)
                .stream()
                .map(bookLogQuestion -> BookLogQuestionResponse.response(bookLogQuestion))
                .collect(Collectors.toList());
    }

    private void validateMemberAuth(Long bookLogId, MemberAuth memberAuth) {
        BookLog bookLog = bookLogRepository.findById(bookLogId)
                .orElseThrow(() -> new ResourceNotFoundException("id에 해당하는 독서 기록 로그가 존재하지 않습니다."));
        BookRecord record = bookRecordRepository.findById(bookLog.getBookRecord().getId()).orElse(null);
        if (!record.getMember().getId().equals(memberAuth.memberId())) {
            throw new ForbiddenException("다른 사람의 독서 기록 로그 질문과 답변을 가져올 수 없습니다.");
        }
    }
}
