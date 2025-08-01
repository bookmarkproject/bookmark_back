package com.example.bookmarkback.book.controller;

import com.example.bookmarkback.book.dto.BookRecordResponse;
import com.example.bookmarkback.book.service.BookRecordService;
import com.example.bookmarkback.global.dto.MemberAuth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/record")
@RequiredArgsConstructor
public class BookRecordController {

    private final BookRecordService bookRecordService;

    @GetMapping("/me")
    public ResponseEntity<List<BookRecordResponse>> getMyBookRecord(MemberAuth memberAuth) {
        List<BookRecordResponse> responseBody = bookRecordService.getMyBookRecord(memberAuth);
        return ResponseEntity.ok(responseBody);
    }
}
