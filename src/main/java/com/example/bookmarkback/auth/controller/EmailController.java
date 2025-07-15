package com.example.bookmarkback.auth.controller;

import com.example.bookmarkback.auth.dto.EmailRequest;
import com.example.bookmarkback.auth.dto.EmailResponse;
import com.example.bookmarkback.auth.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponse> mailSend(@RequestBody @Valid EmailRequest emailDRequest) {
        EmailResponse emailResponse = emailService.mailSend(emailDRequest);
        return ResponseEntity.ok(emailResponse);
    }

    @PostMapping("/check")
    public ResponseEntity<EmailResponse> authNumCheck(@RequestBody @Valid EmailRequest emailRequest) {
        EmailResponse emailResponse = emailService.authNumCheck(emailRequest);
        return ResponseEntity.ok(emailResponse);
    }
}
