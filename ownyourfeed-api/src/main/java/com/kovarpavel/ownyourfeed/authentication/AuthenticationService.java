package com.kovarpavel.ownyourfeed.authentication;

import com.kovarpavel.ownyourfeed.authentication.dto.TokenDTO;
import com.kovarpavel.ownyourfeed.authentication.dto.UserRegistrationDTO;
import com.kovarpavel.ownyourfeed.exception.RefreshTokenExpiredException;
import com.kovarpavel.ownyourfeed.exception.UserExistException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Map<String, RefreshToken> refreshTokens;

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtEncoder = jwtEncoder;
        refreshTokens = new ConcurrentHashMap<>();
    }

    public void registerUser(final UserRegistrationDTO userRegistrationDTO) {
        if (userRepository.existsByUsername(userRegistrationDTO.username()))
            throw new UserExistException("User with username: " + userRegistrationDTO.username() + " already exists.");
        if (userRepository.existsByEmail(userRegistrationDTO.email()))
            throw new UserExistException("User with email: " + userRegistrationDTO.email() + " already exists.");

        userRepository.save(
                new UserEntity(
                        userRegistrationDTO.username(),
                        userRegistrationDTO.email(),
                        bCryptPasswordEncoder.encode(userRegistrationDTO.password())
                )
        );
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        return user.map(value -> new User(
                value.getUsername(),
                value.getPassword(),
                new ArrayList<>()
        )).orElse(null);
    }

    public TokenDTO generateTokens(final Authentication authentication) {
        logger.info("Generating tokens for authenticated user: " + authentication.getName());
        return new TokenDTO(
            generateBearerToken(authentication), 
            generateRefreshToken(authentication));
    }

    public String refreshToken(final String refreshToken) {
        logger.info("Refresh token ...");
        var savedToken = refreshTokens.get(refreshToken);
        if (savedToken != null && Instant.now().isBefore(savedToken.expiresAt())) {
            logger.info("Refreshing token for user: " + savedToken.authentication().getName());
            refreshTokens.put(
                refreshToken, 
                new RefreshToken(savedToken.authentication(), Instant.now().plus(1, ChronoUnit.DAYS)));
            return generateBearerToken(savedToken.authentication());
        }
        throw new RefreshTokenExpiredException("Provided refresh token is invalid or expired, get new tokens via api/auth/token");
    }

    private String generateRefreshToken(final Authentication authentication) {
        var refreshToken = UUID.randomUUID().toString();
        refreshTokens.put(refreshToken, new RefreshToken(authentication, Instant.now().plus(1, ChronoUnit.DAYS)));
        return refreshToken;
    }

    private String generateBearerToken(final Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
