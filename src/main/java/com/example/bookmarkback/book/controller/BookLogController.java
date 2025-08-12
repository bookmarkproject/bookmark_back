package com.example.bookmarkback.book.controller;

import com.example.bookmarkback.book.dto.BookLogRequest;
import com.example.bookmarkback.book.dto.BookLogResponse;
import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.service.BookLogService;
import com.example.bookmarkback.global.dto.MemberAuth;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/log")
@RequiredArgsConstructor
public class BookLogController {

    private final BookLogService bookLogService;

    @GetMapping("/{recordId}")
    public ResponseEntity<List<BookLogResponse>> getBookLogByBookRecordId(@PathVariable Long recordId,
                                                                          MemberAuth memberAuth) {
        List<BookLogResponse> responses = bookLogService.getBookLogByBookRecordId(recordId, memberAuth);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<BookLogResponse> saveBookLog(@Valid @RequestBody BookLogRequest bookLogRequest,
                                                       MemberAuth memberAuth) {
        BookLogResponse response = bookLogService.saveBookLog(bookLogRequest, memberAuth);
        return ResponseEntity.ok(response);
    }
}
