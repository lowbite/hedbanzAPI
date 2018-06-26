package com.hedbanz.hedbanzAPI.constant;

public enum VoteType {
    NO(0), YES(1), WIN(2);

    private int code;

    VoteType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
