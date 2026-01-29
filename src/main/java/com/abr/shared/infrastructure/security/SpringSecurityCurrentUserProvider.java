package com.abr.shared.infrastructure.security;

import com.abr.shared.application.security.AuthenticatedUser;
import com.abr.shared.application.security.CurrentUserProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Optional<AuthenticatedUser> getCurrentUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
