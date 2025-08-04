package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookResponse;
import com.example.bookmarkback.book.entity.Book;
import com.example.bookmarkback.book.repository.BookRepository;
import com.example.bookmarkback.global.exception.ResourceNotFoundException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final AladdinApiService aladdinApiService;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getLatestBooks() throws Exception {
        log.info("최신 책 가져오기 서비스 로직 진입");
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "ItemNewSpecial");

        return getBookListByAladin(additionalParameters);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBestSellers() throws Exception {
        log.info("베스트셀러 가져오기 서비스 로직 진입");

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "BestSeller");

        return getBookListByAladin(additionalParameters);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        log.info("책 id로 책 가져오기 서비스 로직 진입");
        return BookResponse.response(bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id에 대한 책 정보가 존재하지 않습니다.")));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String query) throws Exception {
        log.info("책 검색 서비스 로직 진입");

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("query", query);

        return getAladinBookByQuery(additionalParameters);

    }

    private List<BookResponse> getBookListByAladin(Map<String, Object> parameters) throws Exception {
        List<BookResponse> result = new ArrayList<>();
        Map<String, Object> response = aladdinApiService.getBookList(parameters);
        log.info("API 결과 : {}", response);
        if (response.containsKey("errorCode")) {
            throw new Exception();
        }
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        for (Map<String, Object> book : bookList) {
            log.info("가져온 첵 정보 : {}", BookResponse.response(book).toString());
            result.add(BookResponse.response(book));
        }
        return result;
    }

    private List<BookResponse> getAladinBookByQuery(Map<String, Object> parameters) throws Exception {
        List<BookResponse> result = new ArrayList<>();
        Map<String, Object> response = aladdinApiService.getBookByQuery(parameters);
        log.info("API 결과 : {}", response);
        if (response.containsKey("errorCode")) {
            throw new Exception();
        }
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        for (Map<String, Object> book : bookList) {
            log.info("가져온 첵 정보 : {}", BookResponse.response(book).toString());
            result.add(BookResponse.response(book));
        }
        return result;
    }

}
