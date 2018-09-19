package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public class GlobalFcmPushBuilder extends FcmPushBuilder{
    @Override
    public void setNotification() {
        fcmPush.setNotification(new Notification("New global notification", "Global notification"));
    }

    @Override
    public void setData(Object data) {
        fcmPush.setData(new FcmPush.FcmPushData<>(
                NotificationMessageType.GLOBAL_NOTIFICATION.getCode(),
                data));
    }

    @Override
    public void setPriority() {
        fcmPush.setPriority("normal");
    }
}
