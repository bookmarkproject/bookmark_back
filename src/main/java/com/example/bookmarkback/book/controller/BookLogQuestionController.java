package com.example.bookmarkback.book.controller;

import com.example.bookmarkback.book.dto.BookLogQuestionResponse;
import com.example.bookmarkback.book.entity.BookLogQuestion;
import com.example.bookmarkback.book.service.BookLogQuestionService;
import com.example.bookmarkback.global.dto.MemberAuth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/log/question")
@RequiredArgsConstructor
public class BookLogQuestionController {

    private final BookLogQuestionService bookLogQuestionService;

    @GetMapping("/{bookLogId}")
    public ResponseEntity<List<BookLogQuestionResponse>> getBookLogQuestions(@PathVariable Long bookLogId,
                                                                             MemberAuth memberAuth) {
        List<BookLogQuestionResponse> responses = bookLogQuestionService.getBookLogQuestions(bookLogId, memberAuth);
        return ResponseEntity.ok(responses);
    }

}
