package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.AfkWarning;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/fcm")
public class FcmController {
    private final FcmService fcmService;
    private final UserService userService;

    public FcmController(FcmService fcmService, UserService userService) {
        this.fcmService = fcmService;
        this.userService = userService;
    }

    @PostMapping(value = "user/{userId}/send/afk-warning", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody sendFcmPushAfkWarning(@RequestBody AfkWarning afkWarning, @PathVariable("userId") long userId){
        User user = userService.getUser(userId);
        FcmPush.FcmPushData<AfkWarning> fcmPushData =
                new FcmPush.FcmPushData<>(NotificationMessageType.AFK_WARNING.getCode(), afkWarning);
        FcmPush fcmPush = new FcmPush.Builder()
                .setTo(user.getFcmToken())
                .setNotification(new Notification("Afk warning",
                        "WARNING! In 30 secs you will be kicked out from the room with name" + afkWarning.getRoomName()))
                .setPriority("normal")
                .setData(fcmPushData)
                .build();
        fcmService.sendPushNotification(fcmPush);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @PostMapping(value = "user/{userId}/send/kicked", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody sendFcmPushKicked(@RequestBody AfkWarning afkWarning, @PathVariable("userId") long userId){
        User user = userService.getUser(userId);
        FcmPush.FcmPushData<AfkWarning> fcmPushData =
                new FcmPush.FcmPushData<>(NotificationMessageType.USER_KICKED.getCode(), afkWarning);
        FcmPush fcmPush = new FcmPush.Builder()
                .setTo(user.getFcmToken())
                .setNotification(new Notification("Afk warning",
                        "WARNING! In 30 secs you will be kicked out from the room with name" + afkWarning.getRoomName()))
                .setPriority("normal")
                .setData(fcmPushData)
                .build();
        fcmService.sendPushNotification(fcmPush);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
