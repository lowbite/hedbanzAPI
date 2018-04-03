package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

public class RoomException extends RuntimeException {
    private CustomError error;

    public RoomException(CustomError error){
        this.error = error;
    }

    public CustomError getError(){
        return this.error;
    }
}
