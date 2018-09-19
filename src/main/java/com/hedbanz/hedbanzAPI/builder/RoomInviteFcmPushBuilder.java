package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public class RoomInviteFcmPushBuilder extends FcmPushBuilder{
    @Override
    public void setNotification() {
        fcmPush.setNotification(new Notification("Invite to room", "Friend invited you to room"));
    }

    @Override
    public void setData(Object data) {
        fcmPush.setData(new FcmPush.FcmPushData<>(
                NotificationMessageType.ROOM_INVITE.getCode(),
                data));
    }

    @Override
    public void setPriority() {
        fcmPush.setPriority("normal");
    }
}
