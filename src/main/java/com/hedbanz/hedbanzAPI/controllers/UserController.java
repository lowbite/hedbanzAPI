package com.hedbanz.hedbanzAPI.controllers;

import com.hedbanz.hedbanzAPI.CustomResponseBody;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.UpdateUserData;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.exceptions.UserException;
import com.hedbanz.hedbanzAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> registerUser(@RequestBody User user){
        User foundUser = userService.register(user);
        return new CustomResponseBody<User>(ResultStatus.SUCCESS_STATUS, null, foundUser);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> authenticateUser(@RequestBody User user){
        User foundUser = userService.authenticate(user);
        foundUser.setPassword(null);
        return new CustomResponseBody<User>(ResultStatus.SUCCESS_STATUS, null, foundUser);
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> updateUserData(@RequestBody UpdateUserData userData){
        User updatedUser = userService.updateUserData(userData);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, updatedUser);
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> userNotFound(UserException e){
        return new CustomResponseBody<User>(ResultStatus.ERROR_STATUS, e.getError(), null);
    }
}
