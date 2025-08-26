package com.example.bookmarkback.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

public enum PublicEndpoint {
    MailPostEndpoint(HttpMethod.POST, "/mail/**"),
    AuthPostEndpoint(HttpMethod.POST, "/auth/**"),
    AuthGetEndpoint(HttpMethod.GET, "/auth/**"),
    TestPostEndpoint(HttpMethod.POST, "/test/**");

    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = Arrays.asList(values());
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final HttpMethod httpMethod;
    private final String urlPattern;

    PublicEndpoint(HttpMethod httpMethod, String urlPattern) {
        this.httpMethod = httpMethod;
        this.urlPattern = urlPattern;
    }

    public static boolean isPublicEndpoint(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(publicEndpoint -> publicEndpoint.matches(request));
    }

    public boolean matches(HttpServletRequest request) {
        return httpMethod.matches(request.getMethod()) && ANT_PATH_MATCHER.match(urlPattern, request.getRequestURI());
    }
}
