package com.hedbanz.hedbanzAPI.error;

public enum RoomError implements ApiError {
    WRONG_PASSWORD(301, ErrorMessages.INCORRECT_PASSWORD),
    ROOM_FULL(302, ErrorMessages.ROOM_IS_FULL),
    CANT_START_GAME(303, ErrorMessages.CANT_START_GAME),
    GAME_ALREADY_STARTED(304, ErrorMessages.GAME_HAS_BEEN_ALREADY_STARTED),
    MAX_ACTIVE_ROOMS_NUMBER(305, ErrorMessages.MAX_ACTIVE_ROOMS_NUMBER),
    ROOM_WITH_SUCH_NAME_ALREADY_EXIST(306, ErrorMessages.SUCH_ROOM_ALREADY_EXIST),
    PLAYER_ALREADY_IN_ROOM(307, ErrorMessages.SUCH_PLAYER_ALREADY_IN_ROOM),
    ALREADY_SENT_NEXT_PLAYER(308, ErrorMessages.ALREADY_SENT_NEXT_PLAYER);

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
