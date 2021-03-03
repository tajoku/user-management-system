package com.interview.usermanagementsystem.controller;

import com.interview.usermanagementsystem.api.ApiResponse;
import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.request.UpdateUserRequest;
import com.interview.usermanagementsystem.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    @ApiOperation(value = "Gets a paginated list of all existing users.", notes = "The list is paginated. " +
            "You can provide a page number (default 0) and a page size (default 100). You can also include deactivated users by setting includeDeactivated to true (default false).")
    public ResponseEntity<ApiResponse<Page<User>>> getExistingUsers(@ApiParam(value = "Flag to include deactivated users to the list")
                                                                    @RequestParam(value = "includeDeactivated", required = false) boolean includeDeactivated,
                                                                    Pageable pageable) {
        return createSuccessResponse(userService.getExistingUsers(pageable, includeDeactivated));
    }

    @PostMapping("/api/user")
    @ApiOperation(value = "Registers a user.", notes = "Returns the newly registered user and sends verification link to the specified email address.")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody @Valid CreateUserRequest request) {
        return createSuccessResponse(userService.registerUser(request));
    }

    @GetMapping("/api/user/verify/{code}")
    @ApiOperation(value = "Verifies an existing user.", notes = "Returns verified user.")
    public ResponseEntity<ApiResponse<User>> verifyUser(@ApiParam(value = "The generated code of the user.", required = true) @PathVariable("code") String code) {
        return createSuccessResponse(userService.verifyUser(code));
    }

    @PutMapping("/api/user/{id}")
    @ApiOperation(value = "Updates an existing user.", notes = "You have to provide a valid user ID in the URL. Only specified fields will be updated.")
    public ResponseEntity<ApiResponse<User>> updateUser(@ApiParam(value = "The ID of the user.", required = true) @PathVariable("id") Long id,
                                                        @RequestBody UpdateUserRequest request) {
        return createSuccessResponse(userService.updateUser(id, request));
    }

    @DeleteMapping("/api/user/{id}")
    @ApiOperation(value = "Deactivates an existing user.", notes = "You have to provide a valid user ID in the URL.")
    public ResponseEntity<ApiResponse<User>> deactivateUser(@ApiParam(value = "The ID of the user.", required = true) @PathVariable("id") Long id) {
        return createSuccessResponse(userService.deactivateUser(id));
    }

    private <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(T payload) {
        return ResponseEntity.ok(ApiResponse.<T>builder().result(payload).success(true).build());
    }
}
