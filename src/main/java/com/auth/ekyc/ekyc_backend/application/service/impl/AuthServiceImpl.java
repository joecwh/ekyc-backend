package com.auth.ekyc.ekyc_backend.application.service.impl;

import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.LoginResponse;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterRequest;
import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterResponse;
import com.auth.ekyc.ekyc_backend.application.service.IAuthService;
import com.auth.ekyc.ekyc_backend.common.exception.BusinessException;
import com.auth.ekyc.ekyc_backend.config.AppAuthProperties;
import com.auth.ekyc.ekyc_backend.config.AppBackendProperties;
import com.auth.ekyc.ekyc_backend.domain.entity.Token;
import com.auth.ekyc.ekyc_backend.domain.entity.User;
import com.auth.ekyc.ekyc_backend.domain.enums.TokenStatus;
import com.auth.ekyc.ekyc_backend.domain.enums.TokenType;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.KycApplicationRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.TokenRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.repository.UserRepository;
import com.auth.ekyc.ekyc_backend.infrastructure.security.CryptoService;
import com.auth.ekyc.ekyc_backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppBackendProperties backendProps;
    private final AppAuthProperties authProps;
    private final CryptoService cryptoService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhoneNumber().trim();
        String password = request.getPassword().trim();

        if (userRepository.existsByEmail(email))
            throw new BusinessException("EMAIL_EXISTS", "Email already registered");

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhoneNumberEncrypted(cryptoService.encrypt(phone));
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser;

        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered");
        }

        // invalidate previous verification tokens
        tokenRepository.invalidateUserActiveTokens(savedUser.getId(), TokenType.EMAIL_VERIFICATION);

        // create verification token
        String tokenValue = UUID.randomUUID().toString();

        Token token = new Token();
        token.setUser(savedUser);
        token.setToken(tokenValue);
        token.setTokenType(TokenType.EMAIL_VERIFICATION);
        token.setTokenStatus(TokenStatus.ACTIVE);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(authProps.getVerifyTokenExpMinutes()));
        token.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(token);

        String verifyUrl = backendProps.getBaseUrl()
            + authProps.getEmailVerifyPath()
            + "?token=" + tokenValue;

        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), verifyUrl);
    }

    @Override
    @Transactional
    public void verifyEmail(String tokenValue) {

        Token token = tokenRepository
                .findByTokenAndTokenTypeAndTokenStatus(tokenValue, TokenType.EMAIL_VERIFICATION, TokenStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("TOKEN_INVALID", "Invalid verification token"));

        if (token.getExpiredAt() == null || token.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new BusinessException("TOKEN_EXPIRED", "Verification link expired");

        if (token.getUser().getEmailVerified())
            throw new BusinessException("EMAIL_VERIFIED", "Email already verified");

        User user = token.getUser();
        user.setEmailVerified(true);
        token.setTokenStatus(TokenStatus.USED);
        token.setUsedAt(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword(); // don't trim password on login unless you also trimmed on register

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash()))
            throw new BusinessException("LOGIN_INVALID", "Invalid email or password");

        if (!user.getEmailVerified())
            throw new BusinessException("EMAIL_NOT_VERIFIED", "Please verify your email first");

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(token);
    }
}
