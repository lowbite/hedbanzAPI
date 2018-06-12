package com.hedbanz.hedbanzAPI.service;

public interface FcmPushNotificationService {
    void sendFriendshipRequest(long userId, long friendId);
    void acceptFriendRequest(long userId, long friendId);
}
