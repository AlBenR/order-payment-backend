package com.abr.auth.domain.ports.out;

import com.abr.auth.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
