package com.hedbanz.hedbanzAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.FcmPush;
import com.hedbanz.hedbanzAPI.entity.Notification;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    private final FcmService fcmService;
    private final ConversionService conversionService;

    @Autowired
    public UserController(UserService userService, FcmService fcmService,
                          @Qualifier("APIConversionService") ConversionService conversionService) {
        this.userService = userService;
        this.fcmService = fcmService;
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> registerUser(@RequestBody UserDto userDto) {
        User registeredUser = userService.register(conversionService.convert(userDto, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(registeredUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> authenticateUser(@RequestBody UserDto userDto) {
        User foundUser = userService.authenticate(conversionService.convert(userDto, User.class));
        UserDto resultUser = conversionService.convert(foundUser, UserDto.class);
        resultUser.setSecurityToken(foundUser.getSecurityToken());
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultUser);
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> updateUserData(@RequestBody UserUpdateDto userData) {
        User updatedUser = userService.updateUserData(conversionService.convert(userData, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(updatedUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> getUser(@PathVariable("userId") long userId) {
        User foundUser = userService.getUser(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(foundUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> setUserToken(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        userService.setUserFcmToken(conversionService.convert(userDto, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> releaseUserToken(@PathVariable("userId") long userId) {
        userService.releaseUserFcmToken(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> friendshipRequest(@PathVariable("userId") long userId,
                                                        @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        User friend = userService.getUser(friendId);
        FcmPush fcmPush = new FcmPush.Builder().setTo(friend.getFcmToken())
                .setNotification(new Notification("New friend request!", "Player " + friend.getLogin() +
                        " wants to add to his friendlist."))
                .setData(new FcmPush.FcmPushData(NotificationMessageType.FRIEND.getCode(), null))
                .setPriority("normal")
                .build();
        fcmService.sendPushNotification(fcmPush);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> friendshipAccept(@PathVariable("userId") long userId,
                                                       @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> declineFriendshipRequest(@PathVariable("userId") long userId,
                                                               @PathVariable("friendId") long friendId) {
        userService.deleteFriend(friendId, userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> deleteFriendship(@PathVariable("userId") long userId,
                                                       @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Friend>> getFriendList(@PathVariable("userId") String userId) {
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, userService.getUserFriends(Long.valueOf(userId)));
    }
}
