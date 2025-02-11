package ru.kata.spring.boot_security.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPage(@AuthenticationPrincipal User user, Model model) {
        User userToAdd = new User();

        User userToShow = userService.findByUsername(user.getUsername());

        Set<Role> roles = userToShow.getRoles();
        List<String> userRoles = roles.stream()
                .map(Role::getRoleName)
                .map(name -> name.replace("ROLE_", ""))
                .toList();

        String userToShowRoles = String.join(" ", userRoles);

        model.addAttribute("userToAdd", userToAdd);
        model.addAttribute("userToShow", userToShow);
        model.addAttribute("userToShowRoles", userToShowRoles);
        model.addAttribute("usersList", userService.getAllUsers());
        return "adminHome";
    }

    @GetMapping("/{id}/remove")
    public String deleteUser(@PathVariable(value = "id") long id) {
        Logger log = LogManager.getLogger(AdminController.class);
        log.warn("Попытка удаления пользователя с id " + id);
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @PostMapping("/add")
    public String saveUser(@ModelAttribute("userToAdd") User user,
                           @RequestParam(name = "role", required = false) String roleInput)
    {
        Role role = new Role("ROLE_" + roleInput);
        roleService.saveRole(role);

        if (userService.findByEmail(user.getEmail()) == null) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(role);
            User userToSave = new User(
                    user.getEmail(), user.getPassword(),
                    user.getName(),user.getLastname(), user.getAge(),
                    user.getEmail(), userRoles);
            userService.saveUser(userToSave);
        }

        return "redirect:/admin";
    }

    @PostMapping("/{id}/edit")
    public String updateSelectedUser(@ModelAttribute("userToAdd") User user,
                                     @RequestParam(name = "role", required = false)
                                     String roleInput)
    {
        Logger log = LogManager.getLogger(AdminController.class);
        log.warn("Попытка обновления пользователя, новая роль {} " + roleInput);

        if (user.getUsername() == null) {
            user.setUsername(userService.findByEmail(user.getEmail()).getEmail());
        }

        Role role = new Role("ROLE_" + roleInput);
        roleService.saveRole(role);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRoles(userRoles);

        userService.updateUser(user);
        return "redirect:/admin";
    }
}
