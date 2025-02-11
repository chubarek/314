package ru.kata.spring.boot_security.demo.service;

import org.springframework.data.repository.query.Param;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User findByUsername(String username);
    User findByEmail(String email);

    Optional<User> getUserById(Long id);

    void saveUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);
}
