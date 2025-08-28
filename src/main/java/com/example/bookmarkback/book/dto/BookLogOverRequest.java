package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import com.example.bookmarkback.book.entity.LogType;
import com.example.bookmarkback.global.exception.BadRequestException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record BookLogOverRequest(
        @NotNull(message = "독서 기록 id는 필수 항목입니다.")
        Long bookRecordId,

        @NotEmpty(message = "기록 질문들은 필수 항목입니다.")
        List<@NotBlank(message = "질문 내용은 빈 문자열일 수 없습니다.") String> questions,

        @NotEmpty(message = "기록 답변들은 필수 항목입니다.")
        List<@NotBlank(message = "답변 내용은 빈 문자열일 수 없습니다.") String> answers,

        @NotNull(message = "질문 유형은 필수 항목입니다.")
        String logType
) {
    public BookLog toBookLog(BookRecord bookRecord) {
        return new BookLog(bookRecord, 0L, 1L, LocalDate.now(), 0L, LogType.toEnum(logType));
    }
}
