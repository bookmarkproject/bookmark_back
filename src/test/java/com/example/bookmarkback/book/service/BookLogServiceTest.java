package com.example.bookmarkback.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookmarkback.book.dto.BookLogRequest;
import com.example.bookmarkback.book.dto.BookLogResponse;
import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.entity.RecordStatus;
import com.example.bookmarkback.book.repository.BookLogQuestionRepository;
import com.example.bookmarkback.book.repository.BookLogRepository;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.book.repository.BookRepository;
import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.global.exception.ForbiddenException;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class BookLogServiceTest {

    @Autowired
    BookLogService bookLogService;

    @Autowired
    BookRecordRepository bookRecordRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookLogRepository bookLogRepository;

    @Autowired
    BookLogQuestionRepository bookLogQuestionRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    void tearDown() {
        bookLogQuestionRepository.deleteAllInBatch();
        bookLogRepository.deleteAllInBatch();
        bookRecordRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("현재 기록 중인 책의 기록 로그를 가지고 올 수 있다.")
    void getLogByRecordIdTest() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        Member member2 = getTestMember("aaa@gmail.com", "쿠쿠");
        memberRepository.saveAll(List.of(member1, member2));
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        BookRecord record2 = getTestRecord(member2, books.get(1));
        bookRecordRepository.saveAll(List.of(record1, record2));
        BookLog bookLog1 = getTestBookLog(record1);
        bookLogRepository.save(bookLog1);

        List<BookLogResponse> bookLogByBookRecordId = bookLogService.getBookLogByBookRecordId(record1.getId(),
                new MemberAuth(member1.getId()));

        assertThat(bookLogByBookRecordId.size()).isEqualTo(1);
        assertThat(bookLogByBookRecordId.get(0).pageStart()).isEqualTo(13L);
    }

    @Test
    @DisplayName("기록 중인 독서 내역이 없으면 예외가 발생한다.")
    void getLogByRecordIdWithoutRecord() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        Member member2 = getTestMember("aaa@gmail.com", "쿠쿠");
        memberRepository.saveAll(List.of(member1, member2));
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        BookRecord record2 = getTestRecord(member2, books.get(1));
        bookRecordRepository.saveAll(List.of(record1, record2));
        BookLog bookLog1 = getTestBookLog(record1);
        bookLogRepository.save(bookLog1);

        assertThatThrownBy(
                () -> bookLogService.getBookLogByBookRecordId(record2.getId() + 2, new MemberAuth(member1.getId())))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("해당 id에 대한 독서 기록 정보가 존재하지 않습니다.");

    }

    @Test
    @DisplayName("기록 중인 독서 내역이 없으면 예외가 발생한다.")
    void getLogByRecordIdAnotherMemberId() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        Member member2 = getTestMember("aaa@gmail.com", "쿠쿠");
        memberRepository.saveAll(List.of(member1, member2));
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        BookRecord record2 = getTestRecord(member2, books.get(1));
        bookRecordRepository.saveAll(List.of(record1, record2));
        BookLog bookLog1 = getTestBookLog(record1);
        bookLogRepository.save(bookLog1);

        assertThatThrownBy(
                () -> bookLogService.getBookLogByBookRecordId(record1.getId(), new MemberAuth(member2.getId())))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("다른 사용자의 독서 기록에 접근할 수 없습니다.");
    }

    @Test
    @DisplayName("독서 기록 로그를 저장할 수 있다.")
    @Transactional
    void saveLogTest() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        memberRepository.save(member1);
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        bookRecordRepository.save(record1);
        BookLogRequest request = BookLogRequest.builder()
                .bookRecordId(record1.getId())
                .pageStart(10L)
                .pageEnd(30L)
                .readingTime(25L)
                .isOver(false)
                .questions(List.of("추천", "감상평"))
                .answers(List.of("좋았음", "그럭저럭"))
                .build();

        BookLogResponse response = bookLogService.saveBookLog(request, new MemberAuth(member1.getId()));
        em.flush();
        em.clear();

        assertThat(response.readingTime()).isEqualTo(25L);
        assertThat(record1.getPage()).isEqualTo(30L);
        assertThat(record1.getReadingTime()).isEqualTo(25L);
        assertThat(record1.getStatus()).isEqualTo(RecordStatus.READING);
    }

    @Test
    @DisplayName("독서 기록 로그를 저장할 수 있다. (완독)")
    @Transactional
    void saveLogReadFinishTest() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        memberRepository.save(member1);
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        bookRecordRepository.save(record1);
        BookLogRequest request = BookLogRequest.builder()
                .bookRecordId(record1.getId())
                .pageStart(10L)
                .pageEnd(30L)
                .readingTime(25L)
                .isOver(true)
                .questions(List.of("추천", "감상평"))
                .answers(List.of("좋았음", "그럭저럭"))
                .build();

        BookLogResponse response = bookLogService.saveBookLog(request, new MemberAuth(member1.getId()));
        em.flush();
        em.clear();

        assertThat(response.readingTime()).isEqualTo(25L);
        assertThat(record1.getPage()).isEqualTo(361L);
        assertThat(record1.getReadingTime()).isEqualTo(25L);
        assertThat(record1.getStatus()).isEqualTo(RecordStatus.DONE);
    }

    @Test
    @DisplayName("시작 페이지가 끝 페이지를 넘으면 예외가 발생한다.")
    @Transactional
    void saveLogPageStartMoreThanPageEndTest() {
        Member member1 = getTestMember("asd@gmail.com", "코코");
        memberRepository.save(member1);
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);
        BookRecord record1 = getTestRecord(member1, books.get(0));
        bookRecordRepository.save(record1);
        BookLogRequest request = BookLogRequest.builder()
                .bookRecordId(record1.getId())
                .pageStart(20L)
                .pageEnd(13L)
                .readingTime(25L)
                .isOver(true)
                .questions(List.of("추천", "감상평"))
                .answers(List.of("좋았음", "그럭저럭"))
                .build();

        assertThatThrownBy(() -> bookLogService.saveBookLog(request, new MemberAuth(member1.getId())))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시작 페이지는 끝 페이지를 넘을 수 없습니다.");

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

    private BookLog getTestBookLog(BookRecord bookRecord) {
        return new BookLog(bookRecord, 13L, 31L, LocalDate.of(2025, 7, 30), 31L);
    }
}