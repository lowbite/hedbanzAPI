package com.hedbanz.hedbanzAPI.error;

public enum RoomError {
    WRONG_USER(1, ErrorMessages.NO_SUCH_USER_MESSAGE),
    WRONG_PASSWORD(2, ErrorMessages.INCORRECT_PASSWORD_MESSAGE),
    ROOM_FULL(3, ErrorMessages.ROOM_IS_FULL),
    INCORRECT_INPUT(4, ErrorMessages.INCORRECT_INPUT),
    ALREADY_IN_ROOM(5, ErrorMessages.USER_ALREADY_IN_ROOM),
    NO_SUCH_USER_IN_ROOM(6, ErrorMessages.NO_SUCH_USER_IN_ROOM),
    DB_ERROR(7, ErrorMessages.DB_ERROR),
    CANT_START_GAME(8, ErrorMessages.CANT_START_GAME),
    PLAYERS_ALREADY_GUESS(9, ErrorMessages.GUESSING_IS_ALREADY_STARTED),
    NO_SUCH_ROOM(10, ErrorMessages.NO_SUCH_ROOM),
    NO_SUCH_QUESTION(11, ErrorMessages.NO_SUCH_QUESTION),
    NO_SUCH_PLAYER(12, ErrorMessages.NO_SUCH_PLAYER),
    SUCH_PLAYER_ALREADY_VOTED(12, ErrorMessages.SUCH_PLAYER_ALREADY_VOTED),
    GAME_HAS_BEEN_ALREADY_STARTED(13, ErrorMessages.GAME_HAS_BEEN_ALREADY_STARTED),
    INCORRECT_ROOM_ID(14, ErrorMessages.INCORRECT_ROOM_ID),
    PLAYER_HAVE_ACTIVE_ROOMS_MAX_NUMBER(15, ErrorMessages.MAX_ACTIVE_ROOMS_NUMBER),
    NO_SUCH_MESSAGE(16, ErrorMessages.NO_SUCH_MESSAGE);

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
