package com.kovarpavel.ownyourfeed.authentication;

import java.time.Instant;
import org.springframework.security.core.Authentication;

public record RefreshToken(Authentication authentication, Instant expiresAt) {}
