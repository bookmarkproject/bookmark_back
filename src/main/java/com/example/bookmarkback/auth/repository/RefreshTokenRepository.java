package com.example.bookmarkback.auth.repository;

import com.example.bookmarkback.auth.entity.RefreshToken;
import com.example.bookmarkback.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByMember(Member member);
}
