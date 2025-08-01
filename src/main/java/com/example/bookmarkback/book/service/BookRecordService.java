package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookRecordResponse;
import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;

    @Transactional(readOnly = true)
    public List<BookRecordResponse> getMyBookRecord(MemberAuth memberAuth) {
        log.info("내 기록중인 책 가져오기 서비스 계층 진입");
        List<BookRecordResponse> bookRecordResponses = new ArrayList<>();

        List<BookRecord> bookRecords = bookRecordRepository.findByMemberId(memberAuth.memberId());
        for (BookRecord bookRecord : bookRecords) {
            bookRecordResponses.add(BookRecordResponse.response(
                    BookResponse.response(bookRecord.getBook()), bookRecord));
        }

        return bookRecordResponses;
    }
}
