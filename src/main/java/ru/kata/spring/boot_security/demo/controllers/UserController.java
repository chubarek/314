package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserHomePage(@AuthenticationPrincipal User user, Model model) {
        User userToShow = userService.findByUsername(user.getUsername());
        model.addAttribute("user", userToShow);

        Set<Role> roles = userToShow.getRoles();
        String userRole = roles.stream()
                        .map(Role::getRoleName)
                        .map(name -> name.replace("ROLE_", ""))
                        .collect(Collectors.joining(" "));
        model.addAttribute("usersRole", userRole);
        return "userHome";
    }
}
