package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<String> findAll() {
        List<Role> roles = roleDao.findAll();
        List<String> allRoles = roles.stream()
                .map(Role::getRoleName)
                .map(name -> name.replace("ROLE_", ""))
                .toList();

        return allRoles;
    }

    @Override
    public void saveRole(Role role) {
        roleDao.save(role);
    }

}
