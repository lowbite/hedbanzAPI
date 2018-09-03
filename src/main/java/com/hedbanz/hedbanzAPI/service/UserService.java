package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void logout(User user);

    User register(User user);

    User updateUserData(User user);

    User getUser(Long userId);

    User getUserByLogin(String login);

    List<User> getAllUsers();

    List<Friend> getUserFriends(Long userId);

    List<Friend> getUserAcceptedFriends(Long userId);

    Long getFriendsNumber(Long userId);

    void setUserFcmToken(Long userId, String fcmToken);

    void releaseUserFcmToken(Long userId);

    void addFriend(Long userId, Long friendId);

    void declineFriendship(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void addInvite(Long userId, Long roomId);

    List<Friend> getAcceptedFriendsInRoom(Long userId, Long roomId);

    User updateUserInfo(User user);

    void saveFeedback(Feedback feedback);
}
