package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.entity.error.UserError;

import java.text.MessageFormat;

public class UserException extends RuntimeException {
    private final UserError error;

    public UserException(UserError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public UserException(UserError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    public UserError getError(){
        return error;
    }
}
