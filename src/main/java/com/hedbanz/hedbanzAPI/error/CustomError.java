package com.hedbanz.hedbanzAPI.error;

public class CustomError implements ApiError{
    private int errorCode;
    private String errorMessage;

    public CustomError(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
