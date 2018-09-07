package com.hedbanz.hedbanzAPI.error;

public enum NotFoundError implements ApiError  {
    NO_SUCH_USER_IN_ROOM(101, ErrorMessages.NO_SUCH_USER_IN_ROOM),
    NO_SUCH_QUESTION(102, ErrorMessages.NO_SUCH_QUESTION),
    NO_SUCH_USER(103, ErrorMessages.NO_SUCH_USER_MESSAGE),
    NO_SUCH_ROOM(104, ErrorMessages.NO_SUCH_ROOM),
    NO_SUCH_MESSAGE(105, ErrorMessages.NO_SUCH_MESSAGE),
    NO_SUCH_PLAYER(106, ErrorMessages.NO_SUCH_PLAYER);

    private int errorCode;
    private String errorMessage;
    NotFoundError(int errorCode, String errorMessage){
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
