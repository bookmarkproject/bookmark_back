package com.example.bookmarkback.auth.repository;

import com.example.bookmarkback.auth.entity.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findFirstByEmailOrderByExpiredAtDesc(String email);

    void deleteByEmail(String email);
}
