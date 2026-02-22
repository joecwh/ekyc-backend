package com.auth.ekyc.ekyc_backend.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public record AuthUserPrincipal(UUID userId, String email) {}