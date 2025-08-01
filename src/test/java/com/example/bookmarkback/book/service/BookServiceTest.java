package com.example.bookmarkback.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.repository.BookRecordRepository;
import com.example.bookmarkback.book.repository.BookRepository;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
class BookServiceTest {
    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("id에 해당하는 책의 정보를 가져올 수 있다.")
    void getBookByIdTest() {
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);

        BookResponse bookResponse = bookService.getBookById(books.get(0).getId());

        assertThat(bookResponse.title()).isEqualTo("까미0");
        assertThat(bookResponse.id()).isEqualTo(books.get(0).getId());
    }

    @Test
    @DisplayName("id에 해당하는 책이 존재하지 않으면 예외가 발생한다.")
    void getBookByIdNotResourceTest() {
        List<Book> books = getTestBooks();
        bookRepository.saveAll(books);

        assertThatThrownBy(() -> bookService.getBookById(books.get(books.size() - 1).getId() + 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("해당 id에 대한 책 정보가 존재하지 않습니다.");
    }

    private List<Book> getTestBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            books.add(new Book("까미" + i, "김춘수" + i, "조조출판사", LocalDate.of(2022, 7, 28), "이것은 까미" + i + "의 책입니다.", 361L,
                    "/s", "1241543", 4.5));
        }
        return books;
    }
}