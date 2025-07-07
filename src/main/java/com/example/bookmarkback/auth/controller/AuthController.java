package com.example.bookmarkback.auth.controller;

import com.example.bookmarkback.auth.dto.EmailRequest;
import com.example.bookmarkback.auth.dto.EmailResponse;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.service.AuthService;
import com.example.bookmarkback.member.dto.MemberResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody SignupRequest signupRequest) throws Exception {
        MemberResponse memberResponse = authService.signup(signupRequest);
        return ResponseEntity.created(URI.create("/member/" + memberResponse.id()))
                .body(memberResponse);
    }

    @PostMapping("/mailsend")
    public ResponseEntity<EmailResponse> mailSend(@RequestBody @Valid EmailRequest emailDRequest) {
        EmailResponse emailResponse = authService.mailSend(emailDRequest);
        return ResponseEntity.ok(emailResponse);
    }
}
