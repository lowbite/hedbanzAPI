package com.hedbanz.hedbanzAPI.service;

public interface FCMPushNotificationService {
    void sendFriendshipRequest(long userId, long friendId);
    void acceptFriendRequest(long userId, long friendId);
}
