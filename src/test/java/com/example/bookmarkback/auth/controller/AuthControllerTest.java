package com.example.bookmarkback.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.service.AuthService;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.ValueSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
                .password("test@#52sword")
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
                .profileImage(null)
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일은 필수 항목입니다."));
    }

    @Test
    @DisplayName("회원가입시 비밀번호는 필수 값이다.")
    void notContainPassword() throws Exception {
        Assertions.assertThatThrownBy(() ->
                        SignupRequest.builder()
                                .email("kkk@gmail.com")
                                .password("")
                                .name("김철수")
                                .nickname("포포뇽")
                                .gender("남자")
                                .phoneNumber("01012345678")
                                .birthday(LocalDate.of(1900, 12, 21))
                                .profileImage(null)
                                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("비밀번호는 필수 항목입니다.");

    }

    @Test
    @DisplayName("회원가입시 비밀번호는 8~16자 사이의 길이를 가져야 한다.")
    void passwordLengthTest() throws Exception {
        Assertions.assertThatThrownBy(() ->
                        SignupRequest.builder()
                                .email("kkk@gmail.com")
                                .password("asdsad2")
                                .name("김철수")
                                .nickname("포포뇽")
                                .gender("남자")
                                .phoneNumber("01012345678")
                                .birthday(LocalDate.of(1900, 12, 21))
                                .profileImage(null)
                                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("비밀번호는 8자 이상 16자 이하여야 합니다.");
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
                .profileImage(null)
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
    @ValueSource(strings = {"asdaf@!@$k", "dsfadf@sadk", "135#@%^$34"})
    @DisplayName("비밀번호에 영어, 숫자가 포함이 되지 않으면 예외 발생")
    void notContainNumberOrLetter(String password) throws Exception {
        Assertions.assertThatThrownBy(() ->
                        SignupRequest.builder()
                                .email("kkk@gmail.com")
                                .password(password)
                                .name("김철수")
                                .nickname("포포뇽")
                                .gender("남자")
                                .phoneNumber("01012345678")
                                .birthday(LocalDate.of(1900, 12, 21))
                                .profileImage(null)
                                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("비밀번호는 영어와 숫자가 최소 1개 이상 포함 되어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdaf23535k", "dsf325sadk", "135jjk2334"})
    @DisplayName("비밀번호에 지정된 특수문자가 포함이 되지 않으면 예외 발생")
    void notContainSpecialChar(String password) throws Exception {
        Assertions.assertThatThrownBy(() ->
                        SignupRequest.builder()
                                .email("kkk@gmail.com")
                                .password(password)
                                .name("김철수")
                                .nickname("포포뇽")
                                .gender("남자")
                                .phoneNumber("01012345678")
                                .birthday(LocalDate.of(1900, 12, 21))
                                .profileImage(null)
                                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("비밀번호는 특수문자!@#$%^&*() 중 최소 1개 이상을 포함해야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"asda2@35-35k", "dsf325 sadk", "af124!@41+_"})
    @DisplayName("비밀번호에 허용되지 않는 문자가 있으면 예외 발생")
    void containNotAllowedChar(String password) throws Exception {
        Assertions.assertThatThrownBy(() ->
                        SignupRequest.builder()
                                .email("kkk@gmail.com")
                                .password(password)
                                .name("김철수")
                                .nickname("포포뇽")
                                .gender("남자")
                                .phoneNumber("01012345678")
                                .birthday(LocalDate.of(1900, 12, 21))
                                .profileImage(null)
                                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("허용되지 않는 문자가 포함되어 있습니다.");
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
                .profileImage(null)
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
                .profileImage(null)
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 필수 항목입니다."));
    }

    @Test
    @DisplayName("회원가입시 성별은 필수값이다.")
    void notContainGender() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("dsfad@%34ssf")
                .name("김철수")
                .nickname("포포뇽")
                .gender("")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .profileImage(null)
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("성별은 필수 항목입니다."));
    }

    @Test
    @DisplayName("회원가입시 휴대폰 번호는 필수값이다.")
    void notContainPhoneNumber() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("dsfas35#dasf")
                .name("김철수")
                .nickname("포포뇽")
                .gender("남자")
                .phoneNumber("")
                .birthday(LocalDate.of(1900, 12, 21))
                .profileImage(null)
                .build();

        requestSignup(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("전화번호는 필수 항목입니다."));
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
                .profileImage(null)
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
                LocalDate.of(1900, 12, 21), null);
    }

}