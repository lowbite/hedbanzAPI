package com.hedbanz.hedbanzAPI.constant;

public enum AdvertiseType {
    BANNER(1), VIDEO(2), DEVELOPERS(3);

    private int code;
    AdvertiseType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
