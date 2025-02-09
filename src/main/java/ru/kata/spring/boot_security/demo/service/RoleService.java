package ru.kata.spring.boot_security.demo.service;

import org.springframework.data.repository.query.Param;
import ru.kata.spring.boot_security.demo.models.Role;

public interface RoleService {
    void saveRole(Role role);
}
