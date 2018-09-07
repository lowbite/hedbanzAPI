package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.MessageError;

import java.text.MessageFormat;

public class InputException extends RuntimeException  implements ApiException {
    private final InputError error;

    public InputException(InputError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public InputException(InputError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    public InputError getError(){
        return error;
    }
}
