package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.FcmError;
import java.text.MessageFormat;

public class FcmException extends RuntimeException implements ApiException {
    private final FcmError error;

    public FcmException(FcmError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public FcmException(FcmError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    public FcmError getError(){
        return error;
    }
}
