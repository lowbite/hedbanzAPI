package com.hedbanz.hedbanzAPI.error;

public enum PasswordResetError {
    KEY_WORD_IS_EXPIRED(251, ErrorMessages.KEY_WORD_IS_EXPIRED);

    private int errorCode;
    private String errorMessage;
    PasswordResetError(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }
}
