package com.hedbanz.hedbanzAPI.error;

public enum UserError {
    SUCH_LOGIN_ALREADY_USING(351, ErrorMessages.SUCH_LOGIN_ALREADY_USING_MESSAGE),
    SUCH_EMAIL_ALREADY_USING(352, ErrorMessages.SUCH_EMAIL_ALREADY_USING_MESSAGE),
    CANT_SEND_FRIENDSHIP_REQUEST(353, ErrorMessages.CANT_SEND_FRIENDSHIP_REQUEST),
    ALREADY_FRIENDS(354, ErrorMessages.ALREADY_FRIENDS),
    NOT_FRIENDS(355, ErrorMessages.NOT_FRIENDS);

    private int errorCode;
    private String errorMessage;
    UserError(int errorCode, String errorMessage){
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
