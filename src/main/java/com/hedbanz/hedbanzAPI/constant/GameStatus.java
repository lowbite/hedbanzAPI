package com.hedbanz.hedbanzAPI.constant;

public enum GameStatus {
    WAITING_FOR_PLAYERS(1),
    SETTING_WORDS(2),
    GUESSING_WORDS(3);

    private int code;

    GameStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
