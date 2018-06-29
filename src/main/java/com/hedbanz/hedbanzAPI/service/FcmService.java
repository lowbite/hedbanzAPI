package com.hedbanz.hedbanzAPI.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedbanz.hedbanzAPI.entity.FcmPush;

public interface FcmService {
    void sendPushNotification(FcmPush push);
}
