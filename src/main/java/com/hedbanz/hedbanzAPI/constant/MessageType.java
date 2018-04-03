package com.hedbanz.hedbanzAPI.constant;

public enum MessageType {
    SIMPLE_MESSAGE(7);

    private int code;

    MessageType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
