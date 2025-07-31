package com.example.bookmarkback.book.service;

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

        log.info("요청 URL : {}", requestUrl);

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
