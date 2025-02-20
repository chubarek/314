package ru.kata.spring.boot_security.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.models.UserDTO;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class RestAdminController {

    private final UserService userService;
    private final HttpServletRequest request;

    public RestAdminController(UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @GetMapping("/sendCSRF")
    public ResponseEntity<String> sendCSRF(@AuthenticationPrincipal User user) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return new ResponseEntity<>(csrfToken.getToken(), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);

    }

    @GetMapping("/authorizedUser")
    public ResponseEntity<User> getAuthorizedUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/user/new")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) {
        userService.saveUser(userDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/user/edit")
    public ResponseEntity<User> editUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
