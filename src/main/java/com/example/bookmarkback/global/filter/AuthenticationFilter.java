package com.example.bookmarkback.global.filter;

import com.example.bookmarkback.auth.infra.JwtUtils;
import com.example.bookmarkback.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthenticationFilter(ObjectMapper objectMapper, @Qualifier("loginJwtUtils") JwtUtils jwtUtils) {
        this.objectMapper = objectMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String jwtToken = jwtUtils.extractToken(authorizationHeader);
            Map<String, Object> memberData = jwtUtils.extractMemberIdAndRole(jwtToken);
            request.setAttribute(JwtUtils.JWT_MEMBER_ID_KEY, memberData.get(JwtUtils.JWT_MEMBER_ID_KEY));
            request.setAttribute(JwtUtils.JWT_ROLE_KEY, memberData.get(JwtUtils.JWT_ROLE_KEY));
            log.info("[요청 멤버 ID] : {}", request.getAttribute(JwtUtils.JWT_MEMBER_ID_KEY));
            MDC.put("userId", request.getAttribute(JwtUtils.JWT_MEMBER_ID_KEY).toString());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return PublicEndpoint.isPublicEndpoint(request);
    }
}
