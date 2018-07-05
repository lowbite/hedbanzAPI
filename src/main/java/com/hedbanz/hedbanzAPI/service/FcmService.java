package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.model.FcmPush;

public interface FcmService {
    void sendPushNotification(FcmPush push);
}
