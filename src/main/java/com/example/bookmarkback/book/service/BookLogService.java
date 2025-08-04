package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookLogResponse;
import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.example.bookmarkback.book.entity.BookRecord;
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
public class BookLogService {

    private final BookLogRepository bookLogRepository;

    private final BookRecordRepository bookRecordRepository;

    @Transactional(readOnly = true)
    public List<BookLogResponse> getBookLogByBookRecordId(Long recordId, MemberAuth memberAuth) {

        BookRecord bookRecord = findBookRecord(recordId);
        if (!bookRecord.getMember().getId().equals(memberAuth.memberId())) {
            throw new ForbiddenException("다른 사용자의 독서 기록에 접근할 수 없습니다.");
        }
        return bookLogRepository.findByBookRecord(bookRecord)
                .stream()
                .map(bookLog -> BookLogResponse.response(bookLog))
                .collect(Collectors.toList());
    }

    private BookRecord findBookRecord(Long id) {
        return bookRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id에 대한 독서 기록 정보가 존재하지 않습니다."));
    }
}
