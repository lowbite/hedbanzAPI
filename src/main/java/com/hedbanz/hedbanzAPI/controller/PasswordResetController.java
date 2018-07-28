package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.error.PasswordResetError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.CustomResponseBody;
import com.hedbanz.hedbanzAPI.service.PasswordResetService;
import com.hedbanz.hedbanzAPI.model.PasswordResetData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class PasswordResetController {
    private PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody generateResetPasswordKeyWord(@RequestBody PasswordResetData passwordResetData){
        passwordResetService.generatePasswordResetKeyWord(passwordResetData);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/check-key")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody checkUserKeyWord(@RequestBody PasswordResetData passwordResetData){
        if(passwordResetService.isValidUserKeyWord(passwordResetData))
            return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
        else
            throw ExceptionFactory.create(PasswordResetError.INCORRECT_KEY_WORD);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody resetUserPassword(@RequestBody PasswordResetData passwordResetData){
        passwordResetService.resetUserPassword(passwordResetData);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
