package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record BookLogQuestionResponse(
        Long id,
        String question,
        String answer
) {
    public static BookLogQuestionResponse response(BookLogQuestion bookLogQuestion) {
        return BookLogQuestionResponse.builder()
                .id(bookLogQuestion.getId())
                .question(bookLogQuestion.getQuestion())
                .answer(bookLogQuestion.getAnswer())
                .build();
    }
}
