package com.hedbanz.hedbanzAPI.entity.error;

public enum RegistrationError{
    SUCH_LOGIN_ALREADY_EXIST(5, ErrorMessages.SUCH_LOGIN_ALREADY_EXIST_MESSAGE), SUCH_EMAIL_ALREADY_USING(6, ErrorMessages.SUCH_EMAIL_ALREADY_USING_MESSAGE),
    EMPTY_LOGIN(7, ErrorMessages.EMPTY_LOGIN_MESSAGE), EMPTY_PASSWORD(8, ErrorMessages.EMPTY_PASSWORD_MESSAGE), EMPTY_EMAIL(9, ErrorMessages.EMPTY_EMAIL_MESSAGE);

    private int errorCode;
    private String errorMessage;

    RegistrationError(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public String getErrorMessage(){
        return  this.errorMessage;
    }

}
