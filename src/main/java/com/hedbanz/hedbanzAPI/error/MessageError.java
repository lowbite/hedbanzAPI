package com.hedbanz.hedbanzAPI.error;

public enum MessageError implements ApiError  {
    SUCH_PLAYER_ALREADY_VOTED(201, ErrorMessages.SUCH_PLAYER_ALREADY_VOTED);

    private int errorCode;
    private String errorMessage;
    MessageError(int errorCode, String errorMessage){
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
