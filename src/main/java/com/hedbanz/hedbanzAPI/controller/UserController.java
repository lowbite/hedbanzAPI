package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.transfer.CustomResponseBody;
import com.hedbanz.hedbanzAPI.transfer.FriendDto;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import com.hedbanz.hedbanzAPI.transfer.UserUpdateDto;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.service.FcmPushNotificationService;
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
    private final FcmPushNotificationService fcmPushNotificationService;
    private final ConversionService conversionService;

    @Autowired
    public UserController(UserService userService, FcmPushNotificationService fcmPushNotificationService,
                          @Qualifier("APIConversionService") ConversionService conversionService) {
        this.userService = userService;
        this.fcmPushNotificationService = fcmPushNotificationService;
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> registerUser(@RequestBody UserDto userDto){
        User registeredUser = userService.register(conversionService.convert(userDto, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(registeredUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> authenticateUser(@RequestBody UserDto userDto){
        User foundUser = userService.authenticate(conversionService.convert(userDto, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(foundUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> updateUserData(@RequestBody UserUpdateDto userData){
        User updatedUser = userService.updateUserData(conversionService.convert(userData, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(updatedUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserDto> getUser(@PathVariable("userId") long userId){
        User foundUser = userService.getUser(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(foundUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> setUserToken(@PathVariable("userId") long userId, @RequestBody UserDto userDto){
        userDto.setId(userId);
        userService.setUserFcmToken(conversionService.convert(userDto, User.class));
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> releaseUserToken(@PathVariable("userId") long userId){
        userService.releaseUserFcmToken(userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String > friendshipRequest(@PathVariable("userId") long userId,
                                                         @PathVariable("friendId") long friendId){
        fcmPushNotificationService.sendFriendshipRequest(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String > friendshipAccept(@PathVariable("userId") long userId,
                                                        @PathVariable("friendId") long friendId){
        fcmPushNotificationService.sendFriendshipRequest(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<FriendDto>> getFriendList(@PathVariable("userId") String userId){
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, userService.getUserFriends(Long.valueOf(userId)));
    }
}
