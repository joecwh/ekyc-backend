package com.auth.ekyc.ekyc_backend.domain.entity;

import com.auth.ekyc.ekyc_backend.domain.enums.TokenStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
}
