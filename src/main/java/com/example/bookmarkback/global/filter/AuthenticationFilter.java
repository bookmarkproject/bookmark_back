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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String jwtToken = jwtUtils.extractToken(authorizationHeader);
            Map<String, Object> memberData = jwtUtils.extractMemberIdAndRole(jwtToken);
            request.setAttribute(JwtUtils.JWT_MEMBER_ID_KEY, memberData.get(JwtUtils.JWT_MEMBER_ID_KEY));
            request.setAttribute(JwtUtils.JWT_ROLE_KEY, memberData.get(JwtUtils.JWT_ROLE_KEY));
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
