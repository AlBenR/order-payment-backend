package com.abr.auth.infrastructure.bootstrap;


import com.abr.auth.domain.model.Role;
import com.abr.auth.domain.model.User;
import com.abr.auth.domain.model.UserId;
import com.abr.auth.domain.ports.out.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;

@Component
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.admin.username}")
    private String adminUsername;

    @Value("${auth.admin.password}")
    private String adminPassword;

    public AdminUserInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initAdminUser() {

        // si ya existe, no hacemos nada
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        User admin = new User(
                new UserId(UUID.randomUUID()),
                adminUsername,
                passwordEncoder.encode(adminPassword),
                Set.of(Role.ADMIN)
        );

        userRepository.save(admin);
    }
}