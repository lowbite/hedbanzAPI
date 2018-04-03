package com.hedbanz.hedbanzAPI.entity.error;
/**
 * Copyright 2017. Andrii Chernysh
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public enum UserError {
    NO_SUCH_USER(1, ErrorMessages.NO_SUCH_USER_MESSAGE),
    INCORRECT_PASSWORD(2, ErrorMessages.INCORRECT_PASSWORD_MESSAGE),
    EMPTY_LOGIN(3, ErrorMessages.EMPTY_LOGIN_MESSAGE),
    EMPTY_PASSWORD(4, ErrorMessages.EMPTY_PASSWORD_MESSAGE),
    EMPTY_EMAIL(5, ErrorMessages.EMPTY_EMAIL_MESSAGE),
    SUCH_LOGIN_ALREADY_EXIST(6, ErrorMessages.SUCH_LOGIN_ALREADY_EXIST_MESSAGE),
    SUCH_EMAIL_ALREADY_USING(7, ErrorMessages.SUCH_EMAIL_ALREADY_USING_MESSAGE),
    INVALID_PASSWORD(8, ErrorMessages.INVALID_PASSWORD),
    INVALID_LOGIN(9, ErrorMessages.INVALID_LOGIN),
    INVALID_EMAIL(10, ErrorMessages.INVALID_EMAIL),
    INCORRECT_USER_ID(12, ErrorMessages.NO_SUCH_USER_MESSAGE),
    CANT_SEND_FRIENDSHIP_REQUEST(13, ErrorMessages.CANT_SEND_FRIENDSHIP_REQUEST),
    ALREADY_FRIENDS(14, ErrorMessages.ALREADY_FRIENDS);

    private int errorCode;
    private String errorMessage;
    UserError(int errorCode, String errorMessage){
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
