package com.example.bookmarkback.book.controller;

import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.service.BookService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/latest")
    public ResponseEntity<List<BookResponse>> getLatestBooks() throws Exception {
        List<BookResponse> bookResponses = bookService.getLatestBooks();
        return ResponseEntity.ok(bookResponses);
    }

    @GetMapping("/bestseller")
    public ResponseEntity<List<BookResponse>> getBestSeller() throws Exception {
        List<BookResponse> bookResponses = bookService.getBestSellers();
        return ResponseEntity.ok(bookResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse bookResponse = bookService.getBookById(id);
        return ResponseEntity.ok(bookResponse);
    }
}
