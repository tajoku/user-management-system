package com.interview.usermanagementsystem.api;

import com.interview.usermanagementsystem.exception.UserAlreadyExistsException;
import com.interview.usermanagementsystem.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class RestErrorHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> processUserAlreadyExistsException(UserAlreadyExistsException ex) {

        log.error("Controller Advice is being called. UserAlreadyExistsException thrown", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Void>builder()
                .error(ErrorResponse.builder().message(ex.getMessage()).build()).build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> processUserNotFoundException(UserNotFoundException ex) {

        log.error("Controller Advice is being called. UserNotFoundException thrown", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Void>builder()
                .error(ErrorResponse.builder().message(ex.getMessage()).build()).build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> processException(Exception ex) {

        log.error("Controller Advice is being called. Exception thrown", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                .error(ErrorResponse.builder().message("Unable to complete request.").build()).build());
    }
}
