package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.FcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/fcm")
public class FcmController {
    @Autowired
    private FcmService fcmService;

    @PostMapping(value = "/send", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody sendFcmPushNotification(@RequestBody FcmPush fcmPush){
        fcmService.sendPushNotification(fcmPush);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
