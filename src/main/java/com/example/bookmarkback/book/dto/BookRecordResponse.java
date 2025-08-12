package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookRecordResponse(
        Long id,
        BookResponse bookResponse,
        Long page,
        Long readingTime,
        String status
) {
    public static BookRecordResponse response(BookResponse bookResponse, BookRecord bookRecord) {
        return BookRecordResponse.builder()
                .id(bookRecord.getId())
                .bookResponse(bookResponse)
                .page(bookRecord.getPage())
                .readingTime(bookRecord.getReadingTime())
                .status(bookRecord.getStatus().getName())
                .build();
    }
}
