package com.interview.usermanagementsystem.service;

import com.interview.usermanagementsystem.constants.EmailMessageConstants;
import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.constants.EmailSubjectConstants;
import com.interview.usermanagementsystem.enums.Role;
import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.repository.UserRepository;
import com.interview.usermanagementsystem.request.UpdateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    private static final String APP_URL = "http://localhost:8080";

    @Autowired
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;


    private UserService userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(APP_URL, userRepository, emailService);
    }

    // get existing users

    @Test
    public void shouldReturnPagedListOfAllUsersWhenIncludeDeletedIsTrue() {
        Page<User> users = userService.getExistingUsers(PageRequest.of(0, 5), true);

        assertEquals(5, users.getTotalElements());
    }

    @Test
    public void shouldReturnPagedListOfActiveUsersWhenIncludeDeletedIsFalse() {
        Page<User> users = userService.getExistingUsers(PageRequest.of(0, 5), false);

        assertEquals(3, users.getTotalElements());

        Optional<User> deactivatedUser = users.getContent().stream()
                .filter(user -> user.getStatus().equals(Status.DEACTIVATED))
                .findFirst();

        assertFalse(deactivatedUser.isPresent());
    }

    // Verify User

    @Test
    public void shouldUpdateUserStatusVerifiedFlagAndVerifiedAtWhenUserExists() {
        User registeredUser = userRepository.save(generateRegisteredUser());

        String userCode = registeredUser.getCode();

        assertFalse(registeredUser.isVerified());
        assertEquals(Status.REGISTERED, registeredUser.getStatus());
        assertNull(registeredUser.getVerifiedAt());

        userService.verifyUser(userCode);

        registeredUser = userRepository.findById(registeredUser.getId()).orElse(null);

        assertNotNull(registeredUser);
        assertTrue(registeredUser.isVerified());
        assertEquals(userCode, registeredUser.getCode());
        assertEquals(Status.VERIFIED, registeredUser.getStatus());
        assertNotNull(registeredUser.getVerifiedAt());

        verify(emailService, times(1)).sendText(eq("admin@usermanagementservice.com"), eq(registeredUser.getEmail()),
                eq(EmailSubjectConstants.USER_VERIFICATION), eq(buildEmailBody(registeredUser, EmailMessageConstants.USER_VERIFICATION)));
    }

    @Test
    public void shouldReturnUserWhenUserIsAlreadyVerified() {
        Date verifiedAt = Date.from(LocalDateTime.now().minusDays(4).atZone(ZoneId.systemDefault()).toInstant());

        User verifiedUser = generateVerifiedUser();
        verifiedUser.setVerifiedAt(verifiedAt);

        verifiedUser = userRepository.save(verifiedUser);

        String userCode = verifiedUser.getCode();

        assertTrue(verifiedUser.isVerified());
        assertEquals(Status.VERIFIED, verifiedUser.getStatus());
        assertNotNull(verifiedUser.getVerifiedAt());

        userService.verifyUser(userCode);

        User result = userRepository.findUserByCode(userCode).orElse(null);

        assertEquals(verifiedUser, result);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserToVerifyDoesNotExist() {
        assertThatThrownBy(() -> userService.verifyUser("unknown code")).isInstanceOf(UserNotFoundException.class);
    }


    // Deactivate User

    @Test
    public void shouldUpdateUserStatusAndDeactivatedAtWhenUserExists() {
        User registeredUser = userRepository.save(generateRegisteredUser());

        long userId = registeredUser.getId();

        assertEquals(Status.REGISTERED, registeredUser.getStatus());
        assertNull(registeredUser.getDeactivatedAt());

        userService.deactivateUser(userId);

        registeredUser = userRepository.findById(registeredUser.getId()).orElse(null);

        assertNotNull(registeredUser);
        assertEquals(Status.DEACTIVATED, registeredUser.getStatus());
        assertNotNull(registeredUser.getDeactivatedAt());


        verify(emailService, times(1)).sendText(eq("admin@usermanagementservice.com"), eq(registeredUser.getEmail()),
                eq(EmailSubjectConstants.USER_DEACTIVATION), eq(buildEmailBody(registeredUser, EmailMessageConstants.USER_DEACTIVATION)));
    }

    @Test
    public void shouldReturnUserWhenUserIsAlreadyDeactivated() {
        Date deactivatedAt = Date.from(LocalDateTime.now().minusDays(4).atZone(ZoneId.systemDefault()).toInstant());

        User deactivatedUser = generateDeactivatedUser();
        deactivatedUser.setDeactivatedAt(deactivatedAt);

        deactivatedUser = userRepository.save(deactivatedUser);

        long userId = deactivatedUser.getId();

        assertEquals(Status.DEACTIVATED, deactivatedUser.getStatus());
        assertNotNull(deactivatedUser.getDeactivatedAt());

        userService.deactivateUser(userId);

        User result = userRepository.findById(userId).orElse(null);

        assertEquals(deactivatedUser, result);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserToDeactivateDoesNotExist() {
        assertThatThrownBy(() -> userService.deactivateUser(1234L)).isInstanceOf(UserNotFoundException.class);
    }


    // Register User

    @Test
    public void shouldCreateUserWithStatusRegistered() {
        CreateUserRequest request = CreateUserRequest.builder()
                .title("Mr.")
                .firstname("test")
                .lastname("Tester")
                .email("test@test.com")
                .mobile("122334445")
                .password("password")
                .role(Role.ADMIN)
                .build();

        User result = userService.registerUser(request);

        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getFirstname(), result.getFirstname());
        assertEquals(request.getLastname(), result.getLastname());
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getMobile(), result.getMobile());
        assertEquals(request.getPassword(), result.getPassword());
        assertEquals(request.getRole(), result.getRole());
        assertEquals(Status.REGISTERED, result.getStatus());
        assertNotNull(result.getRegisteredAt());
        assertNotNull(result.getCode());

        String verificationLink = format("%s/api/user/verify/%s", APP_URL, result.getCode());
        String registrationMessage = format(EmailMessageConstants.USER_REGISTRATION, verificationLink);

        verify(emailService, times(1)).sendText(eq("admin@usermanagementservice.com"), eq(result.getEmail()),
                eq(EmailSubjectConstants.USER_REGISTRATION), eq(buildEmailBody(result, registrationMessage)));
    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenUserEmailExistsForUserRegistration() {
        userRepository.save(generateRegisteredUser());
        CreateUserRequest request = CreateUserRequest.builder()
                .title("Mr.")
                .firstname("test")
                .lastname("Tester")
                .email("test@test.com")
                .mobile("122334445")
                .password("password")
                .role(Role.ADMIN)
                .build();

        assertThatThrownBy(() -> userService.registerUser(request)).isInstanceOf(UserAlreadyExistsException.class);
    }

    // Update User

    @Test
    public void shouldUpdateUserWithProvidedAttributes() {
        User user = new User();
        BeanUtils.copyProperties(userRepository.save(generateVerifiedUser()), user);
        UpdateUserRequest request = UpdateUserRequest.builder()
                .lastname("updated lastname")
                .email("updatetest@test.com")
                .password("updated password")
                .build();

        User result = userService.updateUser(user.getId(), request);

        assertEquals(user.getTitle(), result.getTitle());
        assertEquals(user.getFirstname(), result.getFirstname());
        assertNotEquals(request.getFirstname(), result.getFirstname());
        assertEquals(request.getLastname(), result.getLastname());
        assertEquals(request.getEmail(), result.getEmail());
        assertNotEquals(user.getEmail(), result.getEmail());
        assertEquals(request.getPassword(), result.getPassword());
        assertEquals(Status.VERIFIED, result.getStatus());

        verify(emailService, times(1)).sendText(eq("admin@usermanagementservice.com"), eq(result.getEmail()),
                eq(EmailSubjectConstants.USER_UPDATE), eq(buildEmailBody(result, EmailMessageConstants.USER_UPDATE)));
    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenUserEmailExistsForUserUpdate() {
        User user = new User();
        BeanUtils.copyProperties(userRepository.save(generateRegisteredUser()), user);
        UpdateUserRequest request = UpdateUserRequest.builder()
                .title("Mr.")
                .firstname("test")
                .lastname("Tester")
                .email("paulosammy@gmail.com")
                .mobile("122334445")
                .password("password")
                .build();

        assertThatThrownBy(() -> userService.updateUser(user.getId(), request)).isInstanceOf(UserAlreadyExistsException.class);
    }

    private User generateRegisteredUser() {
        return User.builder()
                .role(Role.USER)
                .status(Status.REGISTERED)
                .email("test@test.com")
                .title("Mr.")
                .code("42e8jde")
                .firstname("regtest")
                .lastname("regtester")
                .password("regpass")
                .mobile("090regtester123")
                .build();
    }

    private User generateDeactivatedUser() {
        return User.builder()
                .role(Role.USER)
                .status(Status.DEACTIVATED)
                .email("deacttest@test.com")
                .title("Mrs.")
                .code("122etege")
                .firstname("detest")
                .lastname("detester")
                .password("depass")
                .mobile("090detester123")
                .build();
    }


    private User generateVerifiedUser() {
        return User.builder()
                .role(Role.USER)
                .status(Status.VERIFIED)
                .verified(true)
                .email("vertest@test.com")
                .title("Miss")
                .code("wasw122")
                .firstname("vertest")
                .lastname("vertester")
                .password("verpass")
                .mobile("090vertester123")
                .build();
    }

    private String buildEmailBody(User user,String message) {
        String emailBody = String.format("Dear %s %s,", user.getFirstname(), user.getLastname()) + "\n\n";
        emailBody += message;
        return emailBody;
    }
}
