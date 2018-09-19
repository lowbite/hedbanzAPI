package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public class GuessWordFcmPushBuilder extends FcmPushBuilder{
    @Override
    public void setNotification() {
        fcmPush.setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"));
    }

    @Override
    public void setData(Object data) {
        fcmPush.setData(new FcmPush.FcmPushData<>(
                NotificationMessageType.GUESS_WORD.getCode(),
                data));
    }

    @Override
    public void setPriority() {
        fcmPush.setPriority("normal");
    }
}
