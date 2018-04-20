package com.hedbanz.hedbanzAPI.constant;

public enum MessageType {
    JOINED_USER(1),
    LEFT_USER(2),
    SIMPLE_MESSAGE(7);

    private int code;

    MessageType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
