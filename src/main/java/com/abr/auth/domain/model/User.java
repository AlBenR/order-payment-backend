package com.abr.auth.domain.model;

import java.util.Set;

public class User {

    private final UserId id;
    private final String username;
    private final String passwordHash;
    private final Set<Role> roles;

    public User(UserId id, String username, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    public UserId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public Set<Role> roles() {
        return roles;
    }
}
