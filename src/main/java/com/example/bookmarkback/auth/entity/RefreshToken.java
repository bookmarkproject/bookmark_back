package com.example.bookmarkback.auth.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import com.example.bookmarkback.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken extends BaseEntity {

    public RefreshToken(Member member, String token, LocalDateTime expiredAt) {
        this.member = member;
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public RefreshToken() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
