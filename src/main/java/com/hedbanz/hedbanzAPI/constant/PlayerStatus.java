package com.hedbanz.hedbanzAPI.constant;

public enum PlayerStatus {
    ACTIVE(1),
    AFK(2),
    LEFT(3);

    private int code;

    PlayerStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
