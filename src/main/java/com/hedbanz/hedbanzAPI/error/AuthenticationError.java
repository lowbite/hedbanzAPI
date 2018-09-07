package com.hedbanz.hedbanzAPI.error;

public enum  AuthenticationError implements ApiError {
    INVALID_JWT_TOKEN(401, ErrorMessages.INVALID_JWT_TOKEN),
    EXPIRED_JWT_TOKEN(402, ErrorMessages.EXPIRED_JWT_TOKEN),
    UNSUPPORTED_JWT_TOKEN(403, ErrorMessages.UNSUPPORTED_JWT_TOKEN),
    EMPTY_JWT_TOKEN(404, ErrorMessages.EMPTY_JWT_TOKEN),
    ACCESS_DENIED(405, ErrorMessages.ACCESS_DENIED);

    private int errorCode;
    private String errorMessage;

    AuthenticationError(int errorCode, String errorMessage){
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
