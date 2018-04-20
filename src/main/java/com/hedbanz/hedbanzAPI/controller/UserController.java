package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.DTO.CustomResponseBody;
import com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserUpdateDTO;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.exception.UserException;
import com.hedbanz.hedbanzAPI.service.FCMPushNotificationService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final FCMPushNotificationService fcmPushNotificationService;

    @Autowired
    public UserController(UserService userService, FCMPushNotificationService fcmPushNotificationService) {
        this.userService = userService;
        this.fcmPushNotificationService = fcmPushNotificationService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDTO> registerUser(@RequestBody UserDTO userDTO){
        UserDTO foundUserDTO = userService.register(userDTO);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, foundUserDTO);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDTO> authenticateUser(@RequestBody UserDTO userDTO){
        UserDTO foundUserDTO = userService.authenticate(userDTO);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, foundUserDTO);
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json", value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDTO> updateUserData(@RequestBody UserUpdateDTO userData){
        UserDTO updatedUserDTO = userService.updateUserData(userData);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, updatedUserDTO);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDTO> getUser(@PathVariable("id") long userId){
        UserDTO foundUserDTO = userService.getUser(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, foundUserDTO);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> setUserToken(@RequestParam("userId") long userId, @RequestParam("token") String token){
        userService.setUserToken(userId, token);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/user/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> releaseUserToken(@RequestParam("userId") long userId){
        userService.releaseUserToken(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/friends")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String > friendshipRequest(@RequestParam("userId") long userId, @RequestParam("friendId") long friendId){
        fcmPushNotificationService.sendFriendshipRequest(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/friends")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String > friendshipAccept(@RequestParam("userId") long userId, @RequestParam("friendId") long friendId){
        fcmPushNotificationService.sendFriendshipRequest(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/friends", params = "userId")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<FriendDTO>> getFriendList(@RequestParam("userId") String userId){
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, userService.getUserFriends(Long.valueOf(userId)));
    }
}
