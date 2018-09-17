package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.error.AuthenticationError;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.security.JwtTokenProvider;
import com.hedbanz.hedbanzAPI.service.FeedbackService;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final FeedbackService feedbackService;
    private final UserService userService;
    private final ConversionService conversionService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(FeedbackService feedbackService, UserService userService, @Qualifier("APIConversionService") ConversionService conversionService,
                          AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.feedbackService = feedbackService;
        this.userService = userService;
        this.conversionService = conversionService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }


    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<UserDto> registerUser(@RequestBody UserDto userDto) {
        User registeredUser = userService.register(conversionService.convert(userDto, User.class));
        UserDto resultUserDto = conversionService.convert(registeredUser, UserDto.class);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        resultUserDto.getLogin(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        resultUserDto.setSecurityToken(jwt);
        resultUserDto.setPassword(null);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultUserDto);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<UserDto> authenticateUser(@RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getLogin(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.getUserByLogin(userDto.getLogin());
        UserDto resultUserDto = conversionService.convert(user, UserDto.class);
        String jwt = tokenProvider.generateToken(authentication);
        resultUserDto.setSecurityToken(jwt);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultUserDto);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "update-info", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<UserDto> updateUserInfo(@RequestBody UserDto userDto, Authentication authentication) {
        if (!userDto.getLogin().equals(authentication.getName()))
            throw ExceptionFactory.create(AuthenticationError.ACCESS_DENIED);
        User user = userService.updateUserInfo(conversionService.convert(userDto, User.class));
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(user, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<UserDto> updateUserData(@RequestBody UserUpdateDto userData) {
        User user = userService.getUser(userData.getId());
        if (userData.getOldPassword() != null && !passwordEncoder.matches(userData.getOldPassword(), user.getPassword())) {
            throw ExceptionFactory.create(InputError.INCORRECT_CREDENTIALS);
        }
        User updatedUser = userService.updateUserData(conversionService.convert(userData, User.class));
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null,
                conversionService.convert(updatedUser, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<UserDto> getUser(@PathVariable("userId") long userId) {
        User foundUser = userService.getUser(userId);
        UserDto resultUserDto = conversionService.convert(foundUser, UserDto.class);
        resultUserDto.setFriendsNumber(userService.getFriendsNumber(userId));
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultUserDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/for-user/{forUserId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<Friend> getUserOrFriend(@PathVariable("userId") long userId, @PathVariable("forUserId") long forUserId) {
        List<Friend> friends = userService.getUserFriends(forUserId);
        User user = userService.getUser(userId);
        for (Friend friend : friends) {
            if (friend.getId().equals(user.getUserId())) {
                return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, friend);
            }
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(user, Friend.class));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> setUserToken(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        userService.setUserFcmToken(userId, userDto.getFcmToken());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> releaseUserToken(@PathVariable("userId") long userId) {
        userService.releaseUserFcmToken(userId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/feedback", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> saveFeedback(@RequestBody FeedbackDto feedbackDto) {
        feedbackService.saveFeedback(conversionService.convert(feedbackDto, Feedback.class));
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, true);
    }
}
