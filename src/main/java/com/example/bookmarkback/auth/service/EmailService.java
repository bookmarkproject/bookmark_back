package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.EmailRequest;
import com.example.bookmarkback.auth.dto.EmailResponse;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${host.email}")
    private String hostEmail;

    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public EmailResponse mailSend(EmailRequest emailDRequest) {
        log.info("인증번호 발송 시작");

        Map<String, String> mailInfos = makeMessageForm(emailDRequest.email());
        sendMailLogic(mailInfos.get("setFrom"), mailInfos.get("toMail"), mailInfos.get("title"),
                mailInfos.get("content"));

        saveEmailVerificationInfo(emailDRequest.email(), mailInfos.get("authNum"));

        EmailResponse emailResponse = EmailResponse.response(false);
        return emailResponse;
    }

    @Transactional
    public EmailResponse authNumCheck(EmailRequest emailRequest) {
        log.info("인증번호로 이메일 인증 시작");
        String email = emailRequest.email();
        log.info("인증을 시도한 사용자 이메일 : {}", email);
        String authNum = emailRequest.authNum();
        EmailVerification foundEmailVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                email).orElseThrow(() -> new BadRequestException("인증번호를 발송하지 않은 사용자입니다."));
        validationAuthNum(foundEmailVerification, authNum);

        foundEmailVerification.setVerified(true);
        foundEmailVerification.setVerifiedAt(LocalDateTime.now().plusMinutes(5));
        log.info("최신 인증 만료 시간 : {}", foundEmailVerification.getVerifiedAt());
        log.info("이메일 인증 여부 : {}", foundEmailVerification.isVerified());

        return EmailResponse.response(true);
    }

    private static void validationAuthNum(EmailVerification foundEmailVerification, String authNum) {
        log.info("인증번호 : {}, 사용자 입력 인증번호 : {}", foundEmailVerification.getCode(), authNum);
        log.info("만료 시간 : {}, 현재 시간: {}", foundEmailVerification.getExpiredAt(), LocalDateTime.now());
        if (!foundEmailVerification.getCode().equals(authNum)) {
            throw new BadRequestException("인증번호가 일치하지 않습니다.");
        } else if (foundEmailVerification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("인증시간이 만료되었습니다.");
        }
    }

    private void saveEmailVerificationInfo(String email, String authNum) {
        EmailVerification foundEmailVerification = emailVerificationRepository.findFirstByEmailOrderByExpiredAtDesc(
                email).orElse(null);
        if (foundEmailVerification == null) {
            EmailVerification emailVerification = new EmailVerification(email, authNum,
                    false,
                    LocalDateTime.now().plusMinutes(5), null);
            emailVerificationRepository.save(emailVerification);
            log.info("저장된 인증번호 엔티티 정보: {}", emailVerification.toString());
        } else {
            foundEmailVerification.setCode(authNum);
            log.info("저장된 인증 번호: {}", authNum);
            foundEmailVerification.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        }
    }

    private Map<String, String> makeMessageForm(String toEmail) {
        Map<String, String> mailInfos = new HashMap<>();

        Random r = new Random();
        String authNumber;
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = randomNumber;

        String title = "회원 가입 인증 이메일 입니다.";
        String content =
                "책갈피에 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 입력해주세요";

        mailInfos.put("setFrom", hostEmail);
        mailInfos.put("toMail", toEmail);
        mailInfos.put("title", title);
        mailInfos.put("content", content);
        mailInfos.put("authNum", authNumber);
        return mailInfos;
    }

    private void sendMailLogic(String setFrom, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
            log.info("{} -> {}로 메일 전송 완료", setFrom, toMail);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new BadRequestException("인증번호 발송에 실패했습니다.");
        }
    }

}
