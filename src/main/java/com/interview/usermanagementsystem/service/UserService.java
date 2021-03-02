package com.interview.usermanagementsystem.service;

import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.constants.EmailMessages;
import com.interview.usermanagementsystem.constants.EmailSubject;
import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Page<User> getExistingUsers(Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAllByStatusNotOrderByRegisteredAtAsc(Status.DEACTIVATED, pageable);
    }

    @Transactional
    public User verifyUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.isVerified()) {
            return user;
        }
        user.setVerified(true);
        user.setStatus(Status.VERIFIED);
        user.setVerifiedAt(new Date());

        sendNotification(user, EmailSubject.VERIFICATION, EmailMessages.VERIFICATION);
        return userRepository.save(user);
    }

    @Transactional
    public User deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (user.getStatus().equals(Status.DEACTIVATED)) {
            return user;
        }

        user.setStatus(Status.DEACTIVATED);
        user.setDeactivatedAt(new Date());
        userRepository.save(user);

        sendNotification(user, EmailSubject.DEACTIVATION, EmailMessages.DEACTIVATION);
        return user;
    }

    @Transactional
    public User registerUser(CreateUserRequest request) {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = userRepository.save(User.builder()
                .title(request.getTitle())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(request.getPassword())
                .role(request.getRole())
                .status(Status.REGISTERED)
                .build());

        sendNotification(user, EmailSubject.REGISTRATION, EmailMessages.REGISTRATION);
        return user;
    }

    @Async
    public void sendNotification(User user, String subject, String message) {
        String emailBody = String.format("Dear %s %s,", user.getFirstname(), user.getLastname()) + "\n\n";
        emailBody += message + "\n";
        emailService.sendText("admin@usermanagementservice.com", user.getEmail(), subject,
                emailBody.trim());

    }
}
