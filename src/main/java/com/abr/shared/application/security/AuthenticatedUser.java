package com.abr.shared.application.security;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String username,
        Set<String> roles
) {

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
