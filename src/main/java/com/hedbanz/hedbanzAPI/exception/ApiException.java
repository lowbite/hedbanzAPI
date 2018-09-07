package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.ApiError;

public interface ApiException {

    int getCode();

    String getMessage();

    ApiError getError();
}
