package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record BookLogRequest(
        @NotBlank(message = "독서 기록 id는 필수 항목입니다.")
        Long bookRecordId,
        boolean isOver,
        @NotBlank(message = "시작 페이지는 필수 항목입니다.")
        Long pageStart,
        Long pageEnd,
        @NotBlank(message = "독서 시간은 필수 항목입니다.")
        Long readingTime,
        @NotBlank(message = "기록 질문들은 필수 항목입니다.")
        List<String> questions,
        @NotBlank(message = "기록 답변들은 필수 항목입니다.")
        List<String> answers
) {
    public BookLog toBookLog(BookRecord bookRecord) {
        return new BookLog(bookRecord, pageStart, pageEnd, LocalDate.now(), readingTime);
    }
}
