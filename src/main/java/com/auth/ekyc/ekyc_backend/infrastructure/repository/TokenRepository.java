package com.auth.ekyc.ekyc_backend.infrastructure.repository;

import com.auth.ekyc.ekyc_backend.domain.entity.Token;
import com.auth.ekyc.ekyc_backend.domain.enums.TokenStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByToken(String token);
    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);
    Optional<Token> findByTokenAndTokenTypeAndTokenStatus(String token, TokenType tokenType, TokenStatus tokenStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Token t
        SET t.tokenStatus = com.auth.ekyc.ekyc_backend.domain.enums.TokenStatus.USED
        WHERE t.user.id = :userId
          AND t.tokenType = :type
          AND t.tokenStatus = com.auth.ekyc.ekyc_backend.domain.enums.TokenStatus.ACTIVE
    """)
    void invalidateUserActiveTokens(UUID userId, TokenType type);
}
