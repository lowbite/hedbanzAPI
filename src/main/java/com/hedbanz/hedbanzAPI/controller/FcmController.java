package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.builder.AfkWarningFcmPushBuilder;
import com.hedbanz.hedbanzAPI.builder.FcmPushDirector;
import com.hedbanz.hedbanzAPI.builder.UserKickedFcmPushBuilder;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.AfkWarning;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/fcm")
public class FcmController {
    private final FcmService fcmService;
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public FcmController(FcmService fcmService, UserService userService, MessageService messageService) {
        this.fcmService = fcmService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping(value = "user/{userId}/send/afk-warning", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody sendFcmPushAfkWarning(@RequestBody AfkWarning afkWarning, @PathVariable("userId") long userId){
        User user = userService.getUser(userId);
        FcmPush fcmPush = new FcmPushDirector(new AfkWarningFcmPushBuilder()).buildFcmPush(user.getFcmToken(), afkWarning);
        fcmService.sendPushNotification(fcmPush);
        messageService.addPlayerEventMessage(MessageType.USER_KICK_WARNING, userId, afkWarning.getRoomId());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @PostMapping(value = "user/{userId}/send/kicked", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody sendFcmPushKicked(@RequestBody AfkWarning afkWarning, @PathVariable("userId") long userId){
        User user = userService.getUser(userId);
        FcmPush fcmPush = new FcmPushDirector(new UserKickedFcmPushBuilder())
                .buildFcmPush(user.getFcmToken(), afkWarning);
        fcmService.sendPushNotification(fcmPush);
        messageService.addPlayerEventMessage(MessageType.USER_KICKED, userId, afkWarning.getRoomId());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
