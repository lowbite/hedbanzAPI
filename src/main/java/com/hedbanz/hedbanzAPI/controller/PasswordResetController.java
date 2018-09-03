package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.PasswordResetError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
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
    public ResponseBody generateResetPasswordKeyWord(@RequestBody PasswordResetData passwordResetData) {
        passwordResetService.generatePasswordResetKeyWord(passwordResetData);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/check-key")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody checkUserKeyWord(@RequestBody PasswordResetData passwordResetData) {
        if (passwordResetService.isValidUserKeyWord(passwordResetData))
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
        else
            throw ExceptionFactory.create(InputError.INCORRECT_KEY_WORD);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody resetUserPassword(@RequestBody PasswordResetData passwordResetData) {
        if (passwordResetService.isValidUserKeyWord(passwordResetData)) {
            passwordResetService.resetUserPassword(passwordResetData);
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
        } else
            throw ExceptionFactory.create(InputError.INCORRECT_KEY_WORD);
    }
}
