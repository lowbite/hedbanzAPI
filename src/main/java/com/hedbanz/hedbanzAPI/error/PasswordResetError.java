package com.hedbanz.hedbanzAPI.error;

public enum PasswordResetError {
    EMPTY_LOGIN(1, ErrorMessages.EMPTY_LOGIN_MESSAGE),
    EMPTY_PASSWORD(2, ErrorMessages.EMPTY_PASSWORD_MESSAGE),
    EMPTY_KEY_WORD(3, ErrorMessages.EMPTY_KEY_WORD),
    EMPTY_LOCALE(4, ErrorMessages.EMPTY_LOCALE),
    INCORRECT_LOGIN(5, ErrorMessages.INCORRECT_LOGIN),
    INCORRECT_PASSWORD(6, ErrorMessages.INCORRECT_PASSWORD),
    INCORRECT_KEY_WORD(7, ErrorMessages.INCORRECT_KEY_WORD),
    INCORRECT_LOCALE(8, ErrorMessages.INCORRECT_LOCALE),
    KEY_WORD_IS_EXPIRED(9, ErrorMessages.KEY_WORD_IS_EXPIRED),
    NO_SUCH_USER(10, ErrorMessages.NO_SUCH_USER_MESSAGE);

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
