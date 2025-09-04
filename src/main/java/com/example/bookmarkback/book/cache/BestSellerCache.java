package com.example.bookmarkback.book.cache;

import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.service.BookService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class BestSellerCache {

    private List<BookResponse> bestsellerList;

    private final BookService bookService;

    // 서버 최초 실행 시 1번 실행
    @PostConstruct
    public void init() throws Exception {
        updateBestsellers();
    }

    // 1시간마다 실행 (3600000 ms)
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateBestsellers() throws Exception {
        this.bestsellerList = bookService.getBestSellers();
    }
}
