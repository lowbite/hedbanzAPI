package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.exception.*;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.CustomError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger("DefaultExceptionHandler");

    @ExceptionHandler(InputException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> inputError(InputException e) {
        log.error("Input error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> notFoundError(NotFoundException e) {
        log.error("Not found error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(RoomException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> roomError(RoomException e) {
        log.error("Room error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> userError(UserException e) {
        log.error("User error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(MessageException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> userError(MessageException e) {
        log.error("Vote error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(FcmException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> fcmError(FcmException e) {
        log.error("Fcm error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(PasswordResetException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> passwordResetError(PasswordResetException e) {
        log.error("Password reset error", e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> passwordResetError(BadCredentialsException e) {
        log.error("Bad credentials error");
        return new ResponseBody<>(
                ResultStatus.ERROR_STATUS, new CustomError(
                InputError.INCORRECT_CREDENTIALS.getErrorCode(), InputError.INCORRECT_CREDENTIALS.getErrorMessage()
        ), null);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> authenticationError(InternalAuthenticationServiceException e) {
        log.error("Authentication error");
        if (e.getCause() instanceof NotFoundException) {
            return new ResponseBody<>(
                    ResultStatus.ERROR_STATUS, new CustomError(((NotFoundException) e.getCause()).getCode(), e.getCause().getMessage()), null);
        } else {
            return new ResponseBody<>(ResultStatus.ERROR_STATUS, new CustomError(400, e.getMessage()), null);
        }
    }

    /*@ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> authenticationError(AuthenticationException e) {
        log.error("Authentication error: " + e.getMessage());
        return new ResponseBody<>(ResultStatus.ERROR_STATUS, new CustomError(e.getCode(), e.getMessage()), null);

    }*/

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> error(Exception e) {
        log.error("Unidentified error:" + e.getCause());
        log.error(Arrays.toString(e.getStackTrace()));
        return new ResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(500, "Internal server error"), null);
    }
}
