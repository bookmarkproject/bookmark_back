package com.example.bookmarkback.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.entity.Role;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @AfterEach
    void tearDown() {
        emailVerificationRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("메일 인증을 완료한 사용자는 회원가입이 정상적으로 진행된다.")
    void signupTest() throws Exception {
        //given
        SignupRequest signupRequest = getTestSignupRequest();
        EmailVerification emailVerification = new EmailVerification("kkk@gmail.com", "643654", true,
                LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5));
        emailVerificationRepository.save(emailVerification);

        //when
        MemberResponse memberResponse = authService.signup(signupRequest);
        Member savedMember = memberRepository.findById(memberResponse.id()).orElse(null);

        //then
        assertThat(memberResponse.id()).isEqualTo(savedMember.getId());
        assertThat(memberResponse.email()).isEqualTo(savedMember.getEmail());
        assertThat(memberResponse.role()).isEqualTo("유저");

    }

    @Test
    @DisplayName("메일 인증이 되지 않은 사용자는 회원가입시 예외가 발생한다.")
    void signupWithoutMailAuthentication() throws Exception {
        //given
        SignupRequest signupRequest = getTestSignupRequest();
        EmailVerification emailVerification = new EmailVerification("kkk@gmail.com", "643654", false,
                LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5));
        emailVerificationRepository.save(emailVerification);

        //when,then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이메일 인증을 진행하지 않은 사용자입니다.");
    }

    private SignupRequest getTestSignupRequest() {
        return SignupRequest.builder()
                .email("kkk@gmail.com")
                .password("afkak21@!#2gr")
                .name("김철수")
                .nickname("포포")
                .gender("남자")
                .phoneNumber("01012345678")
                .birthday(LocalDate.of(1900, 12, 21))
                .build();
    }
}