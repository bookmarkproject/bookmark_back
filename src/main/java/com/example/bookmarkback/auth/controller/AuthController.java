package com.example.bookmarkback.auth.controller;

import com.example.bookmarkback.auth.dto.ChangePasswordRequest;
import com.example.bookmarkback.auth.dto.FindEmailRequest;
import com.example.bookmarkback.auth.dto.LoginRequest;
import com.example.bookmarkback.auth.dto.RefreshTokenRequest;
import com.example.bookmarkback.auth.dto.RefreshTokenResponse;
import com.example.bookmarkback.auth.dto.SignupRequest;
import com.example.bookmarkback.auth.service.AuthService;
import com.example.bookmarkback.member.dto.MemberResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        MemberResponse memberResponse = authService.login(loginRequest);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/find/email")
    public ResponseEntity<MemberResponse> findEmail(@Valid @RequestBody FindEmailRequest findEmailRequest) {
        MemberResponse memberResponse = authService.findEmail(findEmailRequest);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/change/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/duplication/nickname")
    public ResponseEntity<Boolean> checkDuplicationNickname(@RequestParam("nickname") String nickname) {
        Boolean isDuplicated = authService.checkNicknameDuplication(nickname);
        return ResponseEntity.ok(isDuplicated);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }
}
