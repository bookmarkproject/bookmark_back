package com.example.bookmarkback.book.service;

import com.example.bookmarkback.book.dto.BookResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final AladdinApiService aladdinApiService;

    public List<BookResponse> getLatestBooks() {
        log.info("최신 책 가져오기 서비스 로직 진입");
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "ItemNewSpecial");

        return getBookListByAladin(additionalParameters);
    }

    public List<BookResponse> getBestSellers() {
        log.info("베스트셀러 가져오기 서비스 로직 진입");

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("querytype", "BestSeller");

        return getBookListByAladin(additionalParameters);
    }

    private List<BookResponse> getBookListByAladin(Map<String, Object> parameters) {
        List<BookResponse> result = new ArrayList<>();
        Map<String, Object> bookDatas = aladdinApiService.getBookList(parameters);
        List<Map<String, Object>> bookList = (List<Map<String, Object>>) bookDatas.get("item");
        for (Map<String, Object> book : bookList) {
            log.info("가져온 첵 정보 : {}", BookResponse.response(book).toString());
            result.add(BookResponse.response(book));
        }
        return result;
    }
}
