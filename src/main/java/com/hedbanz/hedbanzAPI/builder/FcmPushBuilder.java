package com.hedbanz.hedbanzAPI.builder;

import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;

public abstract class FcmPushBuilder {
    protected FcmPush fcmPush;
    public void createFcmPush() {
        fcmPush = new FcmPush();
    }

    public void setTo(String to){
        fcmPush.setTo(to);
    }

    public abstract void setNotification();

    public abstract void setData(Object data);

    public abstract void setPriority();

    public FcmPush getFcmPush() {
        return fcmPush;
    }
}
