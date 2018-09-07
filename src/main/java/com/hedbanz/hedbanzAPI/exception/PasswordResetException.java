package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.FcmError;
import com.hedbanz.hedbanzAPI.error.PasswordResetError;

import java.text.MessageFormat;

public class PasswordResetException extends RuntimeException implements ApiException {
    private final PasswordResetError error;

    public PasswordResetException(PasswordResetError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public PasswordResetException(PasswordResetError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    public PasswordResetError getError(){
        return error;
    }
}
