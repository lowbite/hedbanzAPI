package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public class FriendRequestFcmPushBuilder extends FcmPushBuilder{
    @Override
    public void setNotification() {
        fcmPush.setNotification(new Notification("New friend request!",
                "Someone wants to add to his friend list."));
    }

    @Override
    public void setData(Object data) {
        fcmPush.setData(new FcmPush.FcmPushData<>(
                NotificationMessageType.FRIEND_RUEQEST.getCode(),
                data));
    }

    @Override
    public void setPriority() {
        fcmPush.setPriority("normal");
    }
}
