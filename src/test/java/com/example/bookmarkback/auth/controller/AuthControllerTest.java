package com.example.bookmarkback.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.service.AuthService;
import com.example.bookmarkback.global.filter.AuthenticationFilter;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {
        AuthController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthenticationFilter.class)})
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원 가입을 할 수 있다.")
    void signupTest() throws Exception {
        //given
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("test@#52sword")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        //when, then
        when(authService.signup(request)).thenReturn(MemberResponse.response(getTestMember()));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입시 이메일은 필수 값이다.")
    void notContainEmail() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("")
                .password("te#$%5assword")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일은 필수 항목입니다."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdaf1@321", "dsfadf@12k", "Asdsak!*3k"})
    @DisplayName("회원가입시 영어, 숫자, 특수문자가 포함된다면 요청이 정상적으로 실행된다.")
    void validPasswordProcess(String password) throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password(password)
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        when(authService.signup(request)).thenReturn(MemberResponse.response(getTestMember()));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "sdgsdgsdg", "asfsfaf124214", "asfasf@!#", "214125215", "~~~~~gr+rg31"})
    @DisplayName("유효하지 않은 비밀번호를 입력하면 오류가 발생한다.")
    void notContainNumberOrLetter(String password) throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password(password)
                .name("")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("회원가입시 이름은 필수값이다.")
    void notContainName() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("sgsd#$%y53dfg")
                .name("")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이름은 필수 항목입니다."));
    }

    @Test
    @DisplayName("회원가입시 닉네임은 필수값이다.")
    void notContainNickname() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("d$@%s65ssfgdf")
                .name("김철수")
                .nickname("")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 필수 항목입니다."));
    }

    @ParameterizedTest
    @DisplayName("회원가입시 허용되지 않은 성별이 들어오면 예외가 발생한다.")
    @NullSource
    @ValueSource(strings = {" ", "남저", "요자"})
    void notAllowedGender(String gender) throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("sgsd#$%y53dfg")
                .name("김철수")
                .nickname("포포뇽")
                .gender(gender)
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest());
    }


    @ParameterizedTest
    @ValueSource(strings = {" ", "21312545343", "123456789", "132352343",})
    @NullSource
    @DisplayName("휴대폰 번호가 허용되지 않은 형식이면 예외 발생")
    void phoneNumberContainNotAllowedChar(String phoneNumber) throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("sfhsfh!@44b")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber(phoneNumber)
                .birthday(LocalDate.of(1900, 12, 21)).build();

        requestSignup(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입시 생년월일은 필수값이다.")
    void notContainBirthday() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("dsf3@5fdfd")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(null)
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("생년월일은 필수 항목입니다."));
    }

    private ResultActions requestSignup(SignupRequest request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }

    private Member getTestMember() {
        return new Member("kkk@gmail.com", "testpassword", "김철수", "포포뇽", "남자", "01012345678",
                LocalDate.of(1900, 12, 21), null, null);
    }

}