package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.error.CustomError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.error.UserError;

public class ErrorUtil {
    public static CustomError getError(RoomError roomError){
        return new CustomError(roomError.getErrorCode(), roomError.getErrorMessage());
    }

    public static CustomError getError(UserError userError){
        return new CustomError(userError.getErrorCode(), userError.getErrorMessage());
    }
}
