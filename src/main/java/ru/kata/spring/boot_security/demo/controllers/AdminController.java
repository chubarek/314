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
    public String getUserHomePage(@AuthenticationPrincipal User user, Model model) {
        User userToShow = userService.findByUsername(user.getUsername());
        model.addAttribute("user", userToShow);
        return "admin/adminHome";
    }

    @GetMapping("/allUsers")
    public String showAllUsersPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/allUsers";
    }

    @GetMapping("/add")
    public String showNewUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "admin/addUser";
    }

    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).get();

        model.addAttribute("user", user);
        return "admin/editUser";
    }

    @GetMapping("/{id}/remove")
    public String deleteUser(@PathVariable(value = "id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/allUsers";
    }

    @PostMapping("/add")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(name = "role", required = false) String roleInput)
    {
        Role role = new Role(roleInput);
        roleService.saveRole(role);

        if (userService.findByUsername(user.getUsername()) == null) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(role);
            User userToSave = new User(
                    user.getUsername(), user.getPassword(),
                    user.getName(),user.getLastname(), user.getCity(),
                    user.getAge(), user.getEmail(), userRoles);
            userService.saveUser(userToSave);
        }

        return "redirect:/admin/allUsers";
    }

    @PostMapping("/{id}/edit")
    public String updateSelectedUser(@ModelAttribute("user") User user,
                                     @RequestParam(name = "role", required = false)
                                     String roleInput)
    {
        Logger log = LogManager.getLogger(AdminController.class);
        log.info("Попытка обновления роли пользователя на " + roleInput);

        Role role = new Role(roleInput);
        roleService.saveRole(role);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRoles(userRoles);
        userService.updateUser(user);
        return "redirect:/admin/allUsers";
    }
}
