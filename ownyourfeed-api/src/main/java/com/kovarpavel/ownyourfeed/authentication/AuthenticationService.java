package com.kovarpavel.ownyourfeed.authentication;

import com.kovarpavel.ownyourfeed.exception.UserExistException;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthenticationService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtEncoder = jwtEncoder;
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

    public String generateToken(final Authentication authentication) {
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
