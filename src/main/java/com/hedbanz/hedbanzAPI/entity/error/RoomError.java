package com.hedbanz.hedbanzAPI.entity.error;

public enum RoomError {
    WRONG_USER(1, ErrorMessages.NO_SUCH_USER_MESSAGE),
    WRONG_PASSWORD(2, ErrorMessages.INCORRECT_PASSWORD_MESSAGE),
    ROOM_FULL(3, ErrorMessages.ROOM_IS_FULL),
    INCORRECT_INPUT(4, ErrorMessages.INCORRECT_INPUT),
    ALREADY_IN_ROOM(5, ErrorMessages.USER_ALREADY_IN_ROOM),
    NO_SUCH_USER_IN_ROOM(6, ErrorMessages.NO_SUCH_USER_IN_ROOM),
    DB_ERROR(7, ErrorMessages.DB_ERROR);

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
