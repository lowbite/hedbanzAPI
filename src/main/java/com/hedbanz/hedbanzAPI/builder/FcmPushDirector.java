package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.model.FcmPush;

public class FcmPushDirector {
    private FcmPushBuilder fcmPushBuilder;
    public FcmPushDirector(FcmPushBuilder fcmPushBuilder) {
        this.fcmPushBuilder = fcmPushBuilder;
    }

    public FcmPush buildFcmPush(String to, Object dataToSend){
        fcmPushBuilder.createFcmPush();
        fcmPushBuilder.setTo(to);
        fcmPushBuilder.setData(dataToSend);
        fcmPushBuilder.setNotification();
        fcmPushBuilder.setPriority();
        return fcmPushBuilder.getFcmPush();
    }
}
