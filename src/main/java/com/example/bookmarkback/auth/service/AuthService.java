package com.example.bookmarkback.auth.service;

import com.example.bookmarkback.auth.dto.EmailRequest;
import com.example.bookmarkback.auth.dto.EmailResponse;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.entity.EmailVerification;
import com.example.bookmarkback.auth.repository.EmailVerificationRepository;
import com.example.bookmarkback.global.exception.BadRequestException;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.entity.Member;
import com.example.bookmarkback.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${host.email}")
    private String hostEmail;

    @Value(("${host.password}"))
    private String hostPassword;

    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) throws Exception {
        log.info("회원가입 시작");

        Member member = signupRequest.toMember(encodePassword(signupRequest.password()));
        checkDuplicationEmail(member.getEmail());
        checkDuplicationNickname(member.getNickname());

        try {
            Member savedMember = memberRepository.save(member);
            log.info("회원가입 성공 회원 id : {}", savedMember.getId());
            log.info("회원가입 성공 비밀번호 : {}", savedMember.getPassword());
            return MemberResponse.response(savedMember);
        } catch (Exception e) {
            log.error("서버 예외 발생");
            throw new Exception(e);
        }
    }

    public EmailResponse mailSend(EmailRequest emailDRequest) {
        log.info("인증번호 발송 시작");

        Map<String, String> mailInfos = makeMessageForm(emailDRequest.email());
        sendMailLogic(mailInfos.get("setFrom"), mailInfos.get("toMail"), mailInfos.get("title"),
                mailInfos.get("content"));

        saveEmailVerificationInfo(emailDRequest.email(), mailInfos.get("authNum"));

        EmailResponse emailResponse = EmailResponse.response(false);
        return emailResponse;
    }

    private void saveEmailVerificationInfo(String email, String authNum) {
        EmailVerification emailVerification = new EmailVerification(email, authNum,
                false,
                LocalDateTime.now().plusMinutes(5));

        emailVerificationRepository.save(emailVerification);
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


    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void checkDuplicationEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            log.error("중복 이메일 예외 발생");
            throw new BadRequestException("이미 가입된 이메일이 있습니다.");
        }
    }

    private void checkDuplicationNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            log.error("중복 닉네임 예외 발생");
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        }
    }

}
