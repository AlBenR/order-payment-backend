package com.abr.auth.infrastructure.persistence;

import com.abr.auth.domain.model.Role;
import com.abr.auth.domain.model.User;
import com.abr.auth.domain.model.UserId;
import com.abr.auth.domain.ports.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(
                user.id().value(),
                user.username(),
                user.passwordHash(),
                user.roles().stream()
                        .map(r -> RoleEntity.valueOf(r.name()))
                        .collect(Collectors.toSet())
        );

        jpaRepository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(entity -> new User(
                        new UserId(entity.getId()),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getRoles().stream()
                                .map(r -> Role.valueOf(r.name()))
                                .collect(Collectors.toSet())
                ));
    }
}