package com.hedbanz.hedbanzAPI.constant;

public enum  NotificationMessageType {
    MESSAGE(1),
    SET_WORD(2),
    GUESS_WORD(3),
    FRIEND(4),
    INVITE(5),
    AFK_WARNING(6),
    USER_KICKED(7),
    GAME_OVER(8),
    APP_NEW_VERSION(20);

    private int code;

    NotificationMessageType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
