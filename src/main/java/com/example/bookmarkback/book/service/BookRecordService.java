package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookRecordResponse;
import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookRecordService {

    private final BookRecordRepository bookRecordRepository;

    public List<BookRecordResponse> getMyBookRecord(MemberAuth memberAuth) {
        List<BookRecordResponse> bookRecordResponses = new ArrayList<>();

        List<BookRecord> bookRecords = bookRecordRepository.findByMemberId(memberAuth.memberId());
        for (BookRecord bookRecord : bookRecords) {
            bookRecordResponses.add(BookRecordResponse.response(
                    BookResponse.response(bookRecord.getBook()), bookRecord));
        }
        
        return bookRecordResponses;
    }
}
