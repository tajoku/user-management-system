package com.interview.usermanagementsystem.service;

import com.interview.usermanagementsystem.api.CreateUserRequest;
import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Page<User> getExistingUsers(Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByStatusNotOrderByRegisteredAtAsc(Status.DEACTIVATED, pageable);
    }

    @Transactional
    public User verifyUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setVerified(true);
        user.setStatus(Status.VERIFIED);
        user.setVerifiedAt(new Date());
        return userRepository.save(user);
    }

    @Transactional
    public boolean deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setStatus(Status.DEACTIVATED);
        user.setDeactivatedAt(new Date());
        userRepository.save(user);
        return true;
    }

    @Transactional
    public User registerUser(CreateUserRequest request) {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(User.builder()
                .title(request.getTitle())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(request.getPassword())
                .role(request.getRole())
                .status(Status.REGISTERED)
                .build());
    }
}
