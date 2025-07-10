package com.example.bookmarkback.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bookmarkback.auth.dto.EmailRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.repository.MemberRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:test-application.yml"})
@ActiveProfiles("test")
class EmailServiceTest {

    @Value(value = "${testing.email}")
    private String testingEmail;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @AfterEach
    void tearDown() {
        emailVerificationRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("인증 메일을 전송하면 DB 테이블에 전송 내역이 저장된다.")
    void sendMailSaveVerificationInfoTest() {
        EmailRequest emailRequest = getTestEmailRequest(testingEmail, null);

        emailService.mailSend(emailRequest);
        EmailVerification savedVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                emailRequest.email()).orElse(null);

        assertThat(savedVerification.getEmail()).isEqualTo(emailRequest.email());
        assertThat(savedVerification.getCode().length()).isEqualTo(6);
    }

    @Test
    @DisplayName("이미 인증 내역이 있으면 DB 테이블에 유효시간과 인증번호가 업데이트 된다.")
    void sendMailUpdateVerificationInfoTest() {
        EmailRequest emailRequest = getTestEmailRequest(testingEmail, null);
        emailService.mailSend(emailRequest);
        EmailVerification savedVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                emailRequest.email()).orElse(null);

        emailService.mailSend(emailRequest);
        EmailVerification updatedVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                emailRequest.email()).orElse(null);

        assertThat(savedVerification.getCode()).isNotEqualTo(updatedVerification.getCode());
        assertThat(savedVerification.getExpiredAt()).isNotEqualTo(updatedVerification.getExpiredAt());

    }

    @Test
    @DisplayName("인증번호를 발송하지 않고 인증번호 확인 시도를 하면 예외가 발생한다.")
    void authCheckWithoutSendMail() {
        EmailRequest emailRequest = getTestEmailRequest(testingEmail, "123536");

        assertThatThrownBy(() -> emailService.authNumCheck(emailRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인증번호를 발송하지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("인증번호가 다르면 예외가 발생한다.")
    void authCheckWrongAuthNumber() {
        EmailRequest emailRequest = getTestEmailRequest(testingEmail, "123536");
        EmailVerification emailVerification = new EmailVerification(testingEmail, "123576", false,
                LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5));
        emailVerificationRepository.save(emailVerification);

        assertThatThrownBy(() -> emailService.authNumCheck(emailRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인증번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("인증번호 유효 기간이 만료되면 인증되지 않는다.")
    void authCheckAfterExpiredAt() {
        EmailRequest emailRequest = getTestEmailRequest(testingEmail, "123576");
        EmailVerification emailVerification = new EmailVerification(testingEmail, "123576", false,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(5));
        emailVerificationRepository.save(emailVerification);

        assertThatThrownBy(() -> emailService.authNumCheck(emailRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인증시간이 만료되었습니다.");
    }

    private EmailRequest getTestEmailRequest(String email, String authNum) {
        return EmailRequest.builder()
                .email(email)
                .authNum(authNum)
                .build();
    }
}