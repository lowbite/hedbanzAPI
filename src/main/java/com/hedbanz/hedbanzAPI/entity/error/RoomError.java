package com.hedbanz.hedbanzAPI.entity.error;

public enum RoomError {
    WRONG_USER(1, ErrorMessages.NO_SUCH_USER_MESSAGE), WRONG_PASSWORD(2, ErrorMessages.INCORRECT_PASSWORD_MESSAGE), ROOM_FULL(3, ErrorMessages.ROOM_IS_FULL), DB_ERROR(3, ErrorMessages.DB_ERROR);

    private int errorCode;
    private String errorMessage;
    RoomError(int errorCode, String errorMessage){
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
