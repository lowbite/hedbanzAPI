package com.hedbanz.hedbanzAPI.exceptions;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

public class UserException extends RuntimeException {
    private CustomError error;

    public UserException(CustomError error){
        this.error = error;
    }

    public CustomError getError(){
        return this.error;
    }
}
