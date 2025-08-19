package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookRecordRequest;
import com.example.bookmarkback.book.dto.BookRecordResponse;
import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import jakarta.validation.Valid;
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

    private final MemberRepository memberRepository;
    private final BookRecordRepository bookRecordRepository;
    private final BookService bookService;

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

    @Transactional
    public BookRecordResponse saveBookRecord(BookRecordRequest bookRecordRequest, MemberAuth memberAuth)
            throws Exception {
        log.info("독서 기록하기 서비스 계층 진입");
        Member member = memberRepository.findById(memberAuth.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("멤버 정보가 존재하지 않습니다."));
        Book book = bookService.saveBook(bookRecordRequest);
        log.info("저장된 도서 정보 : {}", book);
        BookRecord bookRecord = bookRecordRequest.toBookRecord(member, book);
        bookRecordRepository.save(bookRecord);
        log.info("저장된 독서 기록 정보 : {}", bookRecord);
        return BookRecordResponse.response(BookResponse.response(book), bookRecord);
    }

    @Transactional(readOnly = true)
    public BookRecordResponse getRecordById(Long id, MemberAuth memberAuth) {
        BookRecord record = bookRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("기록이 존재하지 않습니다."));
        validateMember(record.getMember().getId(), memberAuth.memberId());
        return BookRecordResponse.response(BookResponse.response(record.getBook()), record);
    }

    private void validateMember(Long authorId, Long memberId) {
        if (!authorId.equals(memberId)) {
            throw new ForbiddenException("다른 사용자의 기록에 접근할 수 없습니다.");
        }
    }
}
