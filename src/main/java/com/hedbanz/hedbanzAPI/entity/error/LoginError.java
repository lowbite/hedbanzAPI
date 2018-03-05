package com.hedbanz.hedbanzAPI.entity.error;

public enum LoginError {
    NO_SUCH_USER(1, ErrorMessages.NO_SUCH_USER_MESSAGE), INCORRECT_PASSWORD(2,ErrorMessages.INCORRECT_PASSWORD_MESSAGE),
    EMPTY_LOGIN(3, ErrorMessages.EMPTY_LOGIN_MESSAGE), EMPTY_PASSWORD(4, ErrorMessages.EMPTY_PASSWORD_MESSAGE);

    private int errorCode;
    private String errorMessage;
    LoginError(int errorCode, String errorMessage){
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
