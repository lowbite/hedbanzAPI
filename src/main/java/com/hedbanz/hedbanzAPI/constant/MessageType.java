package com.hedbanz.hedbanzAPI.constant;

public enum MessageType {
    JOINED_USER(1),
    LEFT_USER(2),
    SIMPLE_MESSAGE(7),
    WORD_SETTING(11),
    USER_AFK(13),
    USER_RETURNED(14),
    USER_QUESTION(15),
    USER_WIN(22),
    GAME_OVER(24),
    WAITING_FOR_PLAYERS(25);


    private int code;

    MessageType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
