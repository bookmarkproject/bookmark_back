package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookLogOverRequest;
import com.example.bookmarkback.book.dto.BookLogRequest;
import com.example.bookmarkback.book.dto.BookLogResponse;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.entity.LogType;
import com.example.bookmarkback.book.entity.RecordStatus;
import com.example.bookmarkback.book.repository.BookLogQuestionRepository;
import com.example.bookmarkback.book.repository.BookLogRepository;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
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

    private final BookLogQuestionRepository bookLogQuestionRepository;

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

    @Transactional
    public BookLogResponse saveBookLog(BookLogRequest bookLogRequest, MemberAuth memberAuth) {
        BookRecord record = findBookRecord(bookLogRequest.bookRecordId());
        updateRecord(record, bookLogRequest);
        BookLog bookLog = bookLogRequest.toBookLog(record);
        bookLogRepository.save(bookLog);
        saveBookLogQuestions(bookLogRequest, bookLog);
        return BookLogResponse.response(bookLog);
    }

    @Transactional
    public BookLogResponse saveOverBookLog(@Valid BookLogOverRequest bookLogOverRequest) {
        BookRecord record = findBookRecord(bookLogOverRequest.bookRecordId());
        BookLog bookLog = bookLogOverRequest.toBookLog(record);
        bookLogRepository.save(bookLog);
        saveBookLogOverQuestions(bookLogOverRequest, bookLog);
        return BookLogResponse.response(bookLog);
    }

    private void saveBookLogQuestions(BookLogRequest bookLogRequest, BookLog bookLog) {
        for (int i = 0; i < bookLogRequest.answers().size(); i++) {
            BookLogQuestion bookLogQuestion = new BookLogQuestion(bookLog, bookLogRequest.questions().get(i),
                    bookLogRequest.answers().get(i));
            bookLogQuestionRepository.save(bookLogQuestion);
        }
    }

    private void saveBookLogOverQuestions(BookLogOverRequest bookLogOverRequest, BookLog bookLog) {
        for (int i = 0; i < bookLogOverRequest.answers().size(); i++) {
            BookLogQuestion bookLogQuestion = new BookLogQuestion(bookLog, bookLogOverRequest.questions().get(i),
                    bookLogOverRequest.answers().get(i));
            bookLogQuestionRepository.save(bookLogQuestion);
        }
    }

    private static void updateRecord(BookRecord record, BookLogRequest bookLogRequest) {
        if (bookLogRequest.isOver()) {
            record.setStatus(RecordStatus.DONE);
            record.setPage(record.getBook().getPage());
        } else {
            if (record.getPage() < bookLogRequest.pageEnd()) {
                record.setPage(bookLogRequest.pageEnd());
            }
        }
        record.setReadingTime(record.getReadingTime() + bookLogRequest.readingTime());
    }

    private BookRecord findBookRecord(Long id) {
        return bookRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id에 대한 독서 기록 정보가 존재하지 않습니다."));
    }
}
