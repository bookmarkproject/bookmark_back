package com.example.bookmarkback.member.controller;

import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.member.dto.MemberResponse;
import com.example.bookmarkback.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(MemberAuth memberAuth) {
        MemberResponse memberResponse = memberService.getMyInfo(memberAuth);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(MemberAuth memberAuth) {
        memberService.logout(memberAuth);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id, MemberAuth memberAuth) {
        memberService.deleteMember(id, memberAuth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, MemberAuth memberAuth) {
        try {
            String imageUrl = memberService.uploadFile(file, memberAuth);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfileImage(MemberAuth memberAuth) {
        String response = memberService.getProfileImage(memberAuth);
        return ResponseEntity.ok(response);
    }
}
