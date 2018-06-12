package com.hedbanz.hedbanzAPI.constant;

public enum VoteType {
    YES(1), NO(0);

    private int code;

    VoteType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
