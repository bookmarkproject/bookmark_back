package com.example.bookmarkback.auth.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class EmailVerification extends BaseEntity {

    public EmailVerification() {

    }

    public EmailVerification(String email, String code, boolean isVerified, LocalDateTime expiredAt,
                             LocalDateTime verifiedAt) {
        this.email = email;
        this.code = code;
        this.isVerified = isVerified;
        this.expiredAt = expiredAt;
        this.verifiedAt = verifiedAt;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(length = 10, nullable = false)
    private String code;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

}
