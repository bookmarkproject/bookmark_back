package com.example.bookmarkback.book.service;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladdinApiService {

    @Value("${aladin.key}")
    private String apiKey;

    private final RestClient restClient;

    public Map<String, Object> getBookList(Map<String, Object> parameters) {

        Map<String, Object> baseParameter = getBaseParameter();
        baseParameter.putAll(parameters);

        String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?";
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> param : baseParameter.entrySet()) {
            sj.add(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
        }
        String requestUrl = baseUrl + sj.toString();

        return restClient.get()
                .uri(requestUrl)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> getBookByQuery(Map<String, Object> parameters) {
        Map<String, Object> baseParameter = getBaseParameter();
        baseParameter.putAll(parameters);

        String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?";
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> param : baseParameter.entrySet()) {
            sj.add(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8).replace("+", "%20") + "=" +
                    URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8).replace("+", "%20"));
        }
        String url = baseUrl + sj.toString();

        URI requestUrl = URI.create(url);

        return restClient.get()
                .uri(requestUrl)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> getPageAndRating(Map<String, Object> parameters) {
        Map<String, Object> baseParameter = getBaseParameter();
        baseParameter.putAll(parameters);

        String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?";
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> param : baseParameter.entrySet()) {
            sj.add(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
        }
        String requestUrl = baseUrl + sj.toString();

        return restClient.get()
                .uri(requestUrl)
                .retrieve()
                .body(Map.class);
    }

    private Map<String, Object> getBaseParameter() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ttbkey", apiKey);
        parameters.put("output", "js");
        parameters.put("version", "20131101");
        parameters.put("searchtarget", "book");
        return parameters;
    }
}
