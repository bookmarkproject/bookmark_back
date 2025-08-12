package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.global.exception.BadRequestException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record BookLogRequest(
        @NotNull(message = "독서 기록 id는 필수 항목입니다.")
        Long bookRecordId,

        boolean isOver,

        @NotNull(message = "시작 페이지는 필수 항목입니다.")
        @Min(value = 0, message = "0 이상의 숫자만 입력하세요.")
        Long pageStart,

        @Min(value = 0, message = "0 이상의 숫자만 입력하세요.")
        Long pageEnd,

        @NotNull(message = "독서 시간은 필수 항목입니다.")
        @Min(value = 0, message = "0 이상의 숫자만 입력하세요.")
        Long readingTime,

        @NotEmpty(message = "기록 질문들은 필수 항목입니다.")
        List<@NotBlank(message = "질문 내용은 빈 문자열일 수 없습니다.") String> questions,

        @NotEmpty(message = "기록 답변들은 필수 항목입니다.")
        List<@NotBlank(message = "답변 내용은 빈 문자열일 수 없습니다.") String> answers
) {
    public BookLog toBookLog(BookRecord bookRecord) {
        if (pageStart > pageEnd) {
            throw new BadRequestException("시작 페이지는 끝 페이지를 넘을 수 없습니다.");
        }
        return new BookLog(bookRecord, pageStart, pageEnd, LocalDate.now(), readingTime);
    }
}
