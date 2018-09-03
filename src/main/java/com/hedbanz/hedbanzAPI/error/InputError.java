package com.hedbanz.hedbanzAPI.error;

public enum InputError {
    EMPTY_USER_ID(1, ErrorMessages.EMPTY_USER_ID),
    EMPTY_LOGIN(2, ErrorMessages.EMPTY_LOGIN_MESSAGE),
    EMPTY_PASSWORD(3, ErrorMessages.EMPTY_PASSWORD_MESSAGE),
    EMPTY_EMAIL(4, ErrorMessages.EMPTY_EMAIL_MESSAGE),
    INCORRECT_USER_ID(5, ErrorMessages.INCORRECT_USER_ID),
    INVALID_PASSWORD(6, ErrorMessages.INCORRECT_PASSWORD),
    INVALID_LOGIN(7, ErrorMessages.INCORRECT_LOGIN),
    INVALID_EMAIL(8, ErrorMessages.INCORRECT_EMAIL),
    INCORRECT_CREDENTIALS(9, ErrorMessages.INCORRECT_CREDENTIALS),
    EMPTY_ROOM_ID(10, ErrorMessages.EMPTY_ROOM_ID),
    EMPTY_ROOM_NAME(11, ErrorMessages.EMPTY_ROOM_NAME),
    EMPTY_STICKER_ID(12, ErrorMessages.EMPTY_STICKER_ID),
    EMPTY_ICON_ID(13, ErrorMessages.EMPTY_ICON_ID),
    INCORRECT_ROOM_ID(14, ErrorMessages.INCORRECT_ROOM_ID),
    EMPTY_QUESTION_ID(15, ErrorMessages.EMPTY_QUESTION_ID),
    EMPTY_MESSAGE_TEXT(16, ErrorMessages.EMPTY_MESSAGE_TEXT),
    EMPTY_MESSAGE_TYPE(17, ErrorMessages.EMPTY_MESSAGE_TYPE),
    EMPTY_MESSAGE_SENDER(18, ErrorMessages.EMPTY_SENDER_USER),
    EMPTY_PLAYERS_STATUS(19, ErrorMessages.EMPTY_PLAYERS_STATUS),
    EMPTY_WORD(20, ErrorMessages.EMPTY_WORD),
    EMPTY_VOTE_TYPE(21, ErrorMessages.EMPTY_VOTE_TYPE),
    EMPTY_KEY_WORD(22, ErrorMessages.EMPTY_KEY_WORD),
    EMPTY_LOCALE(23, ErrorMessages.EMPTY_LOCALE),
    INCORRECT_KEY_WORD(24, ErrorMessages.INCORRECT_KEY_WORD),
    INCORRECT_LOCALE(25, ErrorMessages.INCORRECT_LOCALE),
    EMPTY_VERSION_FIELD(26, ErrorMessages.EMPTY_VERSION_FIELD),
    INCORRECT_VERSION_FIELD(27, ErrorMessages.INCORRECT_VERSION_FIELD),
    INCORRECT_FILTER(28, ErrorMessages.INCORRECT_FILTER),
    EMPTY_FCM_TOKEN(29, ErrorMessages.EMPTY_FCM_TOKEN),
    EMPTY_UPDATE_INFO(30, ErrorMessages.EMPTY_UPDATE_INFO),
    EMPTY_DEVICE_MANUFACTURER(31, ErrorMessages.EMPTY_DEVICE_MANUFACTURER),
    EMPTY_DEVICE_MODEL(32, ErrorMessages.EMPTY_DEVICE_MODEL),
    EMPTY_DEVICE_NAME(33, ErrorMessages.EMPTY_DEVICE_NAME),
    EMPTY_FEEDBACK_TEXT(34, ErrorMessages.EMPTY_FEEDBACK_TEXT),
    EMPTY_PRODUCT(35, ErrorMessages.EMPTY_PRODUCT),
    EMPTY_DEVICE_VERSION(36, ErrorMessages.EMPTY_DEVICE_VERSION);

    private int errorCode;
    private String errorMessage;
    InputError(int errorCode, String errorMessage){
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
