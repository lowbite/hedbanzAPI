package com.hedbanz.hedbanzAPI.entity.DTO;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

public class CustomResponseBody<T>{
    private String status;
    private CustomError error;
    private T data;

    public CustomResponseBody(String status,CustomError error, T data){
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public CustomError getError() {
        return error;
    }

    public Object getData() {
        return data;
    }
}
