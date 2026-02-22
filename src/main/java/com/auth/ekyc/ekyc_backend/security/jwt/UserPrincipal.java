package com.auth.ekyc.ekyc_backend.security.jwt;

import java.util.UUID;

public record UserPrincipal(UUID userId, String email) { }