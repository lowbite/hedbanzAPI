package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.entity.error.RoomError;

import java.text.MessageFormat;

public class RoomException extends RuntimeException {
    private final RoomError error;

    public RoomException(RoomError error, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments));
        this.error = error;
    }

    public RoomException(RoomError error, final Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        this.error = error;
    }

    public int getCode() {
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }
}
