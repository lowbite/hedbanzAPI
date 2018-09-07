package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.model.FcmPush;

import java.util.List;

public interface FcmService {
    void sendPushNotification(FcmPush push);
    void sendPushNotificationsToUsers(FcmPush push, List<String> fcmTokens);
}
