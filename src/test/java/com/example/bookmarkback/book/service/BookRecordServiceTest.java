package com.example.bookmarkback.book.service;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookmarkback.book.dto.BookRecordRequest;
import com.example.bookmarkback.book.dto.BookRecordResponse;
import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.entity.RecordStatus;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.book.repository.BookRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class BookRecordServiceTest {

    @Autowired
    BookRecordService bookRecordService;

    @Autowired
    BookRecordRepository bookRecordRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BookRepository bookRepository;

    @AfterEach
    void tearDown() {
        bookRecordRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("기록중인 책을 가져올 수 있다.")
    void getMyBookRecordTest() {
        Member member = getTestMember("kkk@gmail.com", "쿄쿄");
        Member member1 = getTestMember("kkk1@gmail.com", "큐큐");
        memberRepository.save(member);
        memberRepository.save(member1);

        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        for (int i = 0; i < 10; i = i + 3) {
            bookRecordRepository.save(getTestRecord(member, books.get(i)));
        }
        for (int i = 1; i < 10; i = i + 3) {
            bookRecordRepository.save(getTestRecord(member1, books.get(i)));
        }

        List<BookRecordResponse> myBookRecord = bookRecordService.getMyBookRecord(new MemberAuth(member.getId()));

        assertThat(myBookRecord.size()).isEqualTo(4);
        for (BookRecordResponse bookRecordResponse : myBookRecord) {
            assertThat(bookRecordResponse.bookResponse().title()).startsWith("까미");
            assertThat(Long.valueOf(bookRecordResponse.bookResponse().title().substring(2)) % 3).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("기록중인 책이 없으면 빈 리스트를 반환한다.")
    void getMyBookRecordWithoutTest() {
        Member member1 = getTestMember("kkk@gmail.com", "코코");
        Member member2 = getTestMember("kkk1@gmail.com", "큐큐");
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        for (int i = 0; i < 10; i = i + 3) {
            bookRecordRepository.save(getTestRecord(member2, books.get(i)));
        }

        List<BookRecordResponse> myBookRecord = bookRecordService.getMyBookRecord(new MemberAuth(member1.getId()));

        assertThat(myBookRecord.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("독서 기록을 생성할 수 있다.")
    void saveBookRecordTest() throws Exception {
        Member member1 = getTestMember("kkk@gmail.com", "코코");
        memberRepository.save(member1);
        BookRecordRequest request = BookRecordRequest.builder()
                .title("혼모노")
                .author("성해나 (지은이)")
                .contents("하하하")
                .imageUrl("sss")
                .publisher("생능")
                .publishDate(LocalDate.of(2021, 2, 3))
                .isbn("9788936439743")
                .build();

        BookRecordResponse bookRecordResponse = bookRecordService.saveBookRecord(request,
                new MemberAuth(member1.getId()));

        assertThat(bookRecordResponse.page()).isEqualTo(0L);
        assertThat(bookRecordResponse.bookResponse().title()).isEqualTo("혼모노");
        assertThat(bookRecordResponse.bookResponse().contents()).isEqualTo("하하하");
        assertThat(bookRecordResponse.bookResponse().page()).isEqualTo(368L);
        assertThat(bookRecordResponse.bookResponse().rating()).isEqualTo(9.0D);
    }

    private Member getTestMember(String email, String nickname) {
        return new Member(email, "xptmxm!@#123", "김철수", nickname, "남자", "01012345678",
                LocalDate.of(1900, 12, 21), null, null);
    }

    private List<Book> getTestBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            books.add(new Book("까미" + i, "김춘수" + i, "조조출판사", LocalDate.of(2022, 7, 28), "이것은 까미" + i + "의 책입니다.", 361L,
                    "/s", "1241543", 4.5));
        }
        return books;
    }

    private BookRecord getTestRecord(Member member, Book book) {
        return new BookRecord(member, book, 0L, 0L, RecordStatus.READING);
    }
}