package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.models.auth.User;

import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByUsername(String username);
}
