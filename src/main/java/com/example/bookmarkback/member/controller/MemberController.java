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
import org.springframework.web.bind.annotation.RestController;

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
}
