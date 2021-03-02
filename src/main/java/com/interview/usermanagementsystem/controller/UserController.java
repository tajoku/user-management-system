package com.interview.usermanagementsystem.controller;

import com.interview.usermanagementsystem.api.ApiResponse;
import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Page<User>>> getExistingUsers(@RequestParam(value = "includeDeleted", required = false) boolean includeDeleted,
                                                                    Pageable pageable) {
        return createSuccessResponse(userService.getExistingUsers(pageable, includeDeleted));
    }

    @PostMapping("/api/user")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody @Valid CreateUserRequest request) {
        return createSuccessResponse(userService.registerUser(request));
    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<ApiResponse<User>> verifyUser(@PathVariable("id") Long id) {
        return createSuccessResponse(userService.verifyUser(id));
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<ApiResponse<User>> deactivateUser(@PathVariable("id") Long id) {
        return createSuccessResponse(userService.deactivateUser(id));
    }

    private <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(T payload) {
        return ResponseEntity.ok(ApiResponse.<T>builder().result(payload).success(true).build());
    }
}
