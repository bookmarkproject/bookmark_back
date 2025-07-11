package com.example.bookmarkback.global.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
@SpringBootTest
class PublicEndpointTest {

    @ParameterizedTest
    @ValueSource(strings = {"/mail/send", "/mail/check", "/auth/signup", "/auth/login"})
    @DisplayName("public으로 허용된 url은 인증 헤더 검사를 하지 않는다.")
    void publicEndpointTest(String requestUrl) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI(requestUrl);

        assertThat(PublicEndpoint.isPublicEndpoint(request)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/member", "/member/23", "/notice", "/"})
    @DisplayName("public으로 허용되지 않은 url은 인증 헤더 검사를 한다.")
    void notPublicEndpointTest(String requestUrl) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI(requestUrl);

        assertThat(PublicEndpoint.isPublicEndpoint(request)).isFalse();
    }
}