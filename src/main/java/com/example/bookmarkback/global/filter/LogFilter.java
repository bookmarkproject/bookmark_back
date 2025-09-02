package com.example.bookmarkback.global.filter;

import com.example.bookmarkback.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class LogFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            String reqId = request.getHeader("X-Request-ID");
            if (reqId == null || reqId.isEmpty()) {
                reqId = UUID.randomUUID().toString();
            }
            MDC.put("reqId", reqId);

            String url = request.getRequestURI();
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            String method = request.getMethod();
            String clientIp = request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For")
                    : request.getRemoteAddr();

            log.info("==============Request==================");
            log.info("[요청 URL] : {}", url + queryString);
            log.info("[요청 METHOD] : {}", method);
            log.info("[요청 IP] : {}", clientIp);
            log.info("==============Request==================");

            filterChain.doFilter(request, responseWrapper);

            long duration = System.currentTimeMillis() - startTime;

            String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("==============Response=================");
            log.info("[Response Status] : {} ", responseWrapper.getStatus());
            if (responseWrapper.getStatus() >= 400) {
                log.info("[ErrorMessage] : {}", responseBody);
            }
            log.info("[Response Time] : {}ms", duration);
            log.info("==============Response=================");

        } catch (Exception e) {
            log.error("[LogFilter Error]", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ErrorResponse("로깅 필터 오류 발생.")));
        } finally {
            responseWrapper.copyBodyToResponse();
            log.error("테스트 에러 발생");
            MDC.remove("reqId");
        }
    }
}
