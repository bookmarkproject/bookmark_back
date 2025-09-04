package com.example.bookmarkback.book.cache;


import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.service.BookService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class LatestBookCache {

    private List<BookResponse> latestBookList;

    private final BookService bookService;

    // 서버 최초 실행 시 1번 실행
    @PostConstruct
    public void init() throws Exception {
        updateLatestBook();
    }

    // 1시간마다 실행 (3600000 ms)
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateLatestBook() throws Exception {
        this.latestBookList = bookService.getLatestBooks();
    }
}
