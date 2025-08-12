package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookLogResponse(
        Long id,
        Long pageStart,
        Long pageEnd,
        LocalDate readingDate,
        Long readingTime
) {
    public static BookLogResponse response(BookLog bookLog) {
        return BookLogResponse.builder()
                .id(bookLog.getId())
                .pageStart(bookLog.getPageStart())
                .pageEnd(bookLog.getPageEnd())
                .readingDate(bookLog.getReadingDate())
                .readingTime(bookLog.getReadingTime())
                .build();
    }
}
