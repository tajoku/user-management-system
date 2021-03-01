package com.interview.usermanagementsystem.service;

import com.interview.usermanagementsystem.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    // get existing users

    @Test
    public void shouldReturnPagedListOfAllUsersWhenIncludeDeletedIsFalse(){

    }

    @Test
    public void shouldReturnPagedListOfActiveUsersWhenIncludeDeletedIsTrue(){

    }

    @Test
    public void shouldReturnEmptyPagedListWhenNoUsersExist(){

    }

    // Verify User

    @Test
    public void shouldUpdateUserStatusVerifiedFlagAndVerifiedAtWhenUserExists(){

    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserToVerifyDoesNotExist(){

    }


    // Deactivate User

    @Test
    public void shouldUpdateUserStatusAndDeactivatedAtWhenUserExists(){

    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserToDeactivateDoesNotExist(){

    }


    // Register User

    @Test
    public void shouldCreateUserWithStatusRegistered(){

    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenUserEmailExists(){

    }
}
