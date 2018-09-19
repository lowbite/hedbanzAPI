package com.hedbanz.hedbanzAPI.constant;

public enum  NotificationMessageType {
    NEW_MESSAGE(1),
    SET_WORD(2),
    GUESS_WORD(3),
    FRIEND_RUEQEST(4),
    ROOM_INVITE(5),
    AFK_WARNING(6),
    USER_KICKED(7),
    GAME_OVER(8),
    NEW_ROOM_CREATED(9),
    LAST_PLAYER(10),
    ASKING_QUESTION(11),
    APP_NEW_VERSION(20),
    GLOBAL_NOTIFICATION(21);

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
