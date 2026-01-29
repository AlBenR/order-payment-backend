package com.abr.auth.infrastructure.config;

import com.abr.auth.application.service.AuthService;
import com.abr.auth.domain.ports.out.UserRepository;
import com.abr.auth.infrastructure.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfig {

    @Bean
    public AuthService authService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        return new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        return new JwtTokenProvider(secret, expirationMs);
    }
}
