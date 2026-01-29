package com.abr.auth.application.service;

import com.abr.auth.domain.model.Role;
import com.abr.auth.domain.model.User;
import com.abr.auth.domain.model.UserId;
import com.abr.auth.domain.ports.out.UserRepository;
import com.abr.auth.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void register(String username, String rawPassword) {

        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new IllegalStateException("User already exists");
                });

        User user = new User(
                UserId.newId(),
                username,
                passwordEncoder.encode(rawPassword),
                Set.of(Role.CUSTOMER)
        );

        userRepository.save(user);
    }

    public String login(String username, String rawPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtTokenProvider.generateToken(user);
    }
}
