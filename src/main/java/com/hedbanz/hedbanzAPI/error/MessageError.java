package com.hedbanz.hedbanzAPI.error;

public enum MessageError {
    EMPTY_ROOM_ID(1, ErrorMessages.EMPTY_ROOM_ID),
    EMPTY_USER_ID(2, ErrorMessages.EMPTY_USER_ID),
    EMPTY_QUESTION_ID(3, ErrorMessages.EMPTY_QUESTION_ID),
    EMPTY_MESSAGE_TEXT(4, ErrorMessages.EMPTY_MESSAGE_TEXT),
    EMPTY_MESSAGE_TYPE(5, ErrorMessages.EMPTY_MESSAGE_TYPE),
    EMPTY_MESSAGE_SENDER(6, ErrorMessages.EMPTY_SENDER_USER),
    EMPTY_VOTE_TYPE(7, ErrorMessages.EMPTY_VOTE_TYPE),
    NO_SUCH_USER_IN_ROOM(8, ErrorMessages.NO_SUCH_USER_IN_ROOM),
    NO_SUCH_QUESTION(9, ErrorMessages.NO_SUCH_QUESTION),
    NO_SUCH_USER(10, ErrorMessages.NO_SUCH_USER_MESSAGE),
    NO_SUCH_ROOM(11, ErrorMessages.NO_SUCH_ROOM),
    NO_SUCH_MESSAGE(12, ErrorMessages.NO_SUCH_MESSAGE),
    SUCH_PLAYER_ALREADY_VOTED(13, ErrorMessages.SUCH_PLAYER_ALREADY_VOTED);

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
