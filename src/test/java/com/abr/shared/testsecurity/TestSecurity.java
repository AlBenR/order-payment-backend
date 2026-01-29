package com.abr.shared.testsecurity;

import com.abr.shared.application.security.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

public class TestSecurity {

    public static void authenticateAsCustomer(UUID customerId) {
        setUser(new AuthenticatedUser(
                customerId,
                "customer",
                Set.of("CUSTOMER")
        ));
    }

    public static void authenticateAsAdmin() {
        setUser(new AuthenticatedUser(
                UUID.randomUUID(),
                "admin",
                Set.of("ADMIN")
        ));
    }

    private static void setUser(AuthenticatedUser user) {

        var authorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        var authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
