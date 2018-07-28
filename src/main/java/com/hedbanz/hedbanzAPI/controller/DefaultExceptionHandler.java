package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.exception.FcmException;
import com.hedbanz.hedbanzAPI.exception.PasswordResetException;
import com.hedbanz.hedbanzAPI.model.CustomResponseBody;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.CustomError;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger("DefaultExceptionHandler");

    @ExceptionHandler(RoomException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<Room> roomError(RoomException e) {
        log.error("Room error");
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> userError(UserException e) {
        log.error("User error");
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(FcmException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> fcmError(FcmException e) {
        log.error("Fcm error");
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(PasswordResetException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> passwordResetError(PasswordResetException e) {
        log.error("Password reset error");
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(e.getCode(), e.getMessage()), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> error(Exception e) {
        log.error("Unidentified error:" + e.getCause());
        log.error(Arrays.toString(e.getStackTrace()));
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS,
                new CustomError(500, "Internal server error"), null);
    }
}
