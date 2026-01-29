package com.abr.shared.application.security;

import java.util.Optional;

public interface CurrentUserProvider {

    Optional<AuthenticatedUser> getCurrentUser();
}
