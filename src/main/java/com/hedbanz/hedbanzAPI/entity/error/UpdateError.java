package com.hedbanz.hedbanzAPI.entity.error;

public enum UpdateError {
    WRONG_PASSWORD(1, ErrorMessages.INCORRECT_PASSWORD_MESSAGE);

    private int errorCode;
    private String errorMessage;
    UpdateError(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }
}
