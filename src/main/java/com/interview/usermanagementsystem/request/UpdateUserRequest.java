package com.interview.usermanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    private String title;

    private String firstname;

    private String lastname;

    @Email
    private String email;

    private String mobile;

    private String password;
}
