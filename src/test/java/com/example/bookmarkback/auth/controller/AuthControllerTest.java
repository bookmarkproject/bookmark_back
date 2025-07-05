package com.example.bookmarkback.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.service.AuthService;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = {AuthController.class})
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("회원 가입을 할 수 있다.")
    void signupTest() throws Exception {
        //given
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("testpassword")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .profileImage(null)
                .build();

        //when, then
        when(authService.signup(request)).thenReturn(MemberResponse.response(getTestMember()));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입시 이메일은 필수 값이다.")
    void notContainEmail() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("")
                .password("testpassword")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .profileImage(null)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    private Member getTestMember() {
        return new Member("kkk@gmail.com", "testpassword", "김철수", "포포뇽", "남자", "01012345678",
                LocalDate.of(1900, 12, 21), null);
    }


}