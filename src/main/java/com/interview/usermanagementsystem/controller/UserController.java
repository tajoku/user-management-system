package com.interview.usermanagementsystem.controller;

import com.interview.usermanagementsystem.api.CreateUserRequest;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public Page<User> getExistingUsers(@RequestParam(value = "includeDeleted", required = false) boolean includeDeleted,
                                       Pageable pageable) {
        return userService.getExistingUsers(pageable, includeDeleted);
    }

    @PostMapping("/api/user")
    public User registerUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.registerUser(request);
    }

    @PutMapping("/api/user/{id}")
    public User verifyUser(@RequestParam("id") Long id) {
        return userService.verifyUser(id);
    }

    @DeleteMapping("/api/user/{id}")
    public User deactivateUser(@RequestParam("id") Long id) {
        return userService.deactivateUser(id);
    }
}
