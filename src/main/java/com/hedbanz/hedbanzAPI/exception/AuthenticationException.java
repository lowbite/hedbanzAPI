package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.AuthenticationError;
import com.hedbanz.hedbanzAPI.error.InputError;

import java.text.MessageFormat;

public class AuthenticationException extends RuntimeException{
    private final AuthenticationError error;

    public AuthenticationException(AuthenticationError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public AuthenticationException(AuthenticationError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    public AuthenticationError getError(){
        return error;
    }
}
