package com.interview.usermanagementsystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.usermanagementsystem.api.ApiResponse;
import com.interview.usermanagementsystem.enums.Role;
import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import com.interview.usermanagementsystem.model.User;
import com.interview.usermanagementsystem.request.CreateUserRequest;
import com.interview.usermanagementsystem.request.UpdateUserRequest;
import com.interview.usermanagementsystem.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.platform.commons.util.StringUtils.isBlank;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;


    @Test
    public void shouldReturnNewUserWhenRegisterUserIsSuccessful() throws Exception {
        when(userService.registerUser(any(CreateUserRequest.class))).thenReturn(new User());
        MvcResult result = registerUser(CreateUserRequest.builder()
                .title("Mr.")
                .firstname("test")
                .lastname("Tester")
                .email("test@test.com")
                .mobile("122334445")
                .password("password")
                .role(Role.ADMIN)
                .build());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        ApiResponse<User> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<User>>() {
                });
        assertTrue(response.isSuccess());
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void shouldReturnRegisterUserErrorResponseWhenEmailAlreadyExists() throws Exception {
        when(userService.registerUser(any(CreateUserRequest.class))).thenThrow(new UserAlreadyExistsException());
        MvcResult result = registerUser(CreateUserRequest.builder()
                .title("Mr.")
                .firstname("test")
                .lastname("Tester")
                .email("test@test.com")
                .mobile("122334445")
                .password("password")
                .role(Role.ADMIN)
                .build());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        if (!isBlank(body)) {
            ApiResponse<Void> response = objectMapper.readValue(body, new TypeReference<ApiResponse<Void>>() {
            });
            assertFalse(response.isSuccess());
            assertNull(response.getResult());
            assertNotNull(response.getError());
        }
    }


    @Test
    public void shouldReturnUserWhenUserUpdateIsSuccessful() throws Exception {
        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(new User());
        MvcResult result = updateUser(10L, new UpdateUserRequest());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        ApiResponse<User> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<User>>() {
                });
        assertTrue(response.isSuccess());
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void shouldReturnUpdateUserErrorResponseWhenEmailAlreadyExists() throws Exception {
        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenThrow(new UserAlreadyExistsException());
        MvcResult result = updateUser(11L, new UpdateUserRequest());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        if (!isBlank(body)) {
            ApiResponse<Void> response = objectMapper.readValue(body, new TypeReference<ApiResponse<Void>>() {
            });
            assertFalse(response.isSuccess());
            assertNull(response.getResult());
            assertNotNull(response.getError());
        }
    }

    @Test
    public void shouldReturnUserWhenVerifyingUser() throws Exception {
        when(userService.verifyUser(anyString())).thenReturn(new User());
        MvcResult result = verifyUser("test");
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        ApiResponse<User> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<User>>() {
                });
        assertTrue(response.isSuccess());
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void shouldReturnVerifyUserErrorResponseWhenUserIdDoesNotExist() throws Exception {
        when(userService.verifyUser(anyString())).thenThrow(new UserNotFoundException());
        MvcResult result = verifyUser("code");
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        if (!isBlank(body)) {
            ApiResponse<Void> response = objectMapper.readValue(body, new TypeReference<ApiResponse<Void>>() {
            });
            assertFalse(response.isSuccess());
            assertNull(response.getResult());
            assertNotNull(response.getError());
        }
    }


    @Test
    public void shouldReturnUserWhenDeactivateUser() throws Exception {
        when(userService.deactivateUser(anyLong())).thenReturn(new User());
        MvcResult result = deactivateUser(11L);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        ApiResponse<User> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<User>>() {
                });
        assertTrue(response.isSuccess());
        assertNotNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void shouldReturnDeactivateUserErrorResponseWhenUserIdDoesNotExist() throws Exception {
        when(userService.deactivateUser(anyLong())).thenThrow(new UserNotFoundException());
        MvcResult result = deactivateUser(10L);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        if (!isBlank(body)) {
            ApiResponse<Void> response = objectMapper.readValue(body, new TypeReference<ApiResponse<Void>>() {
            });
            assertFalse(response.isSuccess());
            assertNull(response.getResult());
            assertNotNull(response.getError());
        }
    }

    @Test
    public void shouldReturnAnErrorResponseWhenAnInternalServerErrorOccurs() throws Exception {
        when(userService.getExistingUsers(any(Pageable.class), anyBoolean())).thenThrow(new RuntimeException());
        MvcResult result = getExistingUsers(PageRequest.of(0, 1));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        if (!isBlank(body)) {
            ApiResponse<Void> response = objectMapper.readValue(body, new TypeReference<ApiResponse<Void>>() {
            });
            assertFalse(response.isSuccess());
            assertNull(response.getResult());
            assertNotNull(response.getError());
            assertEquals("Unable to complete request.", response.getError().getMessage());
        }
    }

    private MvcResult getExistingUsers(Pageable pageable) throws Exception {
        return mvc.perform(get("/api/users", pageable, true)).andReturn();
    }

    private MvcResult registerUser(CreateUserRequest request) throws Exception {
        return mvc.perform(
                post("/api/user")
                        .content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    private MvcResult updateUser(Long id, UpdateUserRequest request) throws Exception {
        return mvc.perform(put("/api/user/" + id)
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    private MvcResult verifyUser(String code) throws Exception {
        return mvc.perform(get("/api/user/verify/" + code)).andReturn();
    }

    private MvcResult deactivateUser(Long id) throws Exception {
        return mvc.perform(delete("/api/user/" + id)).andReturn();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
