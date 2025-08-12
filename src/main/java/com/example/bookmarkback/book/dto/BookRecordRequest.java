package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.entity.RecordStatus;
import com.example.bookmarkback.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookRecordRequest(
        @NotBlank(message = "isbn은 필수 항목입니다.")
        String isbn,
        @NotBlank(message = "제목은 필수 항목입니다.")
        String title,
        @NotBlank(message = "저자는 필수 항목입니다.")
        String author,

        String contents,
        String imageUrl,

        @NotBlank(message = "출판사는 필수 항목입니다.")
        String publisher,
        @NotNull(message = "출판일은 필수 항목입니다.")
        LocalDate publishDate
) {

    public Book toBook() {
        return new Book(title, author, publisher, publishDate, contents, null, imageUrl, isbn, null);
    }

    public BookRecord toBookRecord(Member member, Book book) {
        return new BookRecord(member, book, 0L, 0L, RecordStatus.READING);
    }
}
