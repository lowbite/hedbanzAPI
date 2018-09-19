package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public class LastPlayerFcmPushBuilder extends FcmPushBuilder{
    @Override
    public void setNotification() {
        fcmPush.setNotification(new Notification("Last player in room!", "You are the last player in room"));
    }

    @Override
    public void setData(Object data) {
        fcmPush.setData(new FcmPush.FcmPushData<>(
                NotificationMessageType.LAST_PLAYER.getCode(),
                data));
    }

    @Override
    public void setPriority() {
        fcmPush.setPriority("normal");
    }
}