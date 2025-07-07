package com.example.bookmarkback.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Entity
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            initialValue = 1,
            allocationSize = 50
    )
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(length = 10, nullable = false)
    private String code;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}
