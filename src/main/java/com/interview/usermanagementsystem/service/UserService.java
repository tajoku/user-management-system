package com.interview.usermanagementsystem.service;

import com.google.common.base.Strings;
import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.constants.EmailMessageConstants;
import com.interview.usermanagementsystem.constants.EmailSubjectConstants;
import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.repository.UserRepository;
import com.interview.usermanagementsystem.request.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

import static java.lang.String.format;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final String appUrl;

    @Autowired
    public UserService(@Value("${app.url:}") String appUrl, UserRepository userRepository, EmailService emailService) {
        this.appUrl = appUrl;
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
    public User verifyUser(String code) {
        User user = userRepository.findUserByCode(code).orElseThrow(UserNotFoundException::new);
        if (user.isVerified()) {
            return user;
        }
        user.setVerified(true);
        user.setStatus(Status.VERIFIED);
        user.setVerifiedAt(new Date());

        sendNotification(user, EmailSubjectConstants.USER_VERIFICATION, EmailMessageConstants.USER_VERIFICATION);
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

        sendNotification(user, EmailSubjectConstants.USER_DEACTIVATION, EmailMessageConstants.USER_DEACTIVATION);
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
                .code(UUID.randomUUID().toString())
                .status(Status.REGISTERED)
                .build());

        String verificationLink = format("%s/api/user/verify/%s", appUrl, user.getCode());
        String registrationMessage = format(EmailMessageConstants.USER_REGISTRATION, verificationLink);

        sendNotification(user, EmailSubjectConstants.USER_REGISTRATION, registrationMessage);
        return user;
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!Strings.isNullOrEmpty(request.getEmail()) && userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        if (!Strings.isNullOrEmpty(request.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (!Strings.isNullOrEmpty(request.getTitle())) {
            user.setTitle(request.getTitle());
        }

        if (!Strings.isNullOrEmpty(request.getFirstname())) {
            user.setFirstname(request.getFirstname());
        }

        if (!Strings.isNullOrEmpty(request.getLastname())) {
            user.setLastname(request.getLastname());
        }

        if (!Strings.isNullOrEmpty(request.getMobile())) {
            user.setMobile(request.getMobile());
        }

        if (!Strings.isNullOrEmpty(request.getPassword())) {
            user.setPassword(request.getPassword());
        }

        user = userRepository.save(user);

        sendNotification(user, EmailSubjectConstants.USER_UPDATE, EmailMessageConstants.USER_UPDATE);
        return user;
    }

    @Async
    public void sendNotification(User user, String subject, String message) {
        String emailBody = String.format("Dear %s %s,", user.getFirstname(), user.getLastname()) + "\n\n";
        emailBody += message;
        emailService.sendText("admin@usermanagementservice.com", user.getEmail(), subject,
                emailBody.trim());

    }
}
