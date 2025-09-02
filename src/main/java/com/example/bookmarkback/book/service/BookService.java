package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookRecordRequest;
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
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "ItemNewSpecial");

        return getBookListByAladin(additionalParameters);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBestSellers() throws Exception {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "BestSeller");

        return getBookListByAladin(additionalParameters);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return BookResponse.response(bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id에 대한 책 정보가 존재하지 않습니다.")));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String query) throws Exception {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("query", query);

        return getAladinBookByQuery(additionalParameters);

    }

    private List<BookResponse> getBookListByAladin(Map<String, Object> parameters) throws Exception {
        List<BookResponse> result = new ArrayList<>();
        Map<String, Object> response = aladdinApiService.getBookList(parameters);
        if (response.containsKey("errorCode")) {
            throw new Exception();
        }
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        for (Map<String, Object> book : bookList) {
            result.add(BookResponse.response(book));
        }
        return result;
    }

    private List<BookResponse> getAladinBookByQuery(Map<String, Object> parameters) throws Exception {
        List<BookResponse> result = new ArrayList<>();
        Map<String, Object> response = aladdinApiService.getBookByQuery(parameters);
        if (response.containsKey("errorCode")) {
            throw new Exception();
        }
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        for (Map<String, Object> book : bookList) {
            result.add(BookResponse.response(book));
        }
        return result;
    }

    public Book saveBook(BookRecordRequest bookRecordRequest) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("itemIdType", "ISBN13");
        parameters.put("optResult", "ratinginfo");
        parameters.put("itemId", bookRecordRequest.isbn());
        Map<String, Object> response = aladdinApiService.getPageAndRating(parameters);
        if (response.containsKey("errorCode")) {
            throw new Exception();
        }
        Long page = extractPage(response);
        Double rating = extractRating(response);
        Book book = bookRecordRequest.toBook();
        book.setPage(page);
        book.setRating(rating);

        bookRepository.save(book);

        return book;
    }

    private Long extractPage(Map<String, Object> response) {
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        Map<String, Object> subInfo = (Map<String, Object>) bookList.get(0).get("subInfo");
        Integer page = (Integer) subInfo.get("itemPage");
        return page.longValue();
    }

    private Double extractRating(Map<String, Object> response) {
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) response.get("item");
        Map<String, Object> subInfo = (Map<String, Object>) bookList.get(0).get("subInfo");
        Map<String, Object> ratingInfo = (Map<String, Object>) subInfo.get("ratingInfo");
        Object ratingScore = ratingInfo.get("ratingScore");
        if (ratingScore instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }
}
