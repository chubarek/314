package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserHomePage(@AuthenticationPrincipal User user, Model model) {
        User userToShow = userService.findByUsername(user.getUsername());
        model.addAttribute("user", userToShow);
        return "admin/adminHome";
    }

    @GetMapping("/allUsers")
    public String sjowAllUsersPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/allUsers";
    }
}
