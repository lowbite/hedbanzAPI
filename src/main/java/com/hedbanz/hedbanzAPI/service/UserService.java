package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.Friend;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User authenticate(User user);

    Optional<UserDetails> findUserByToken(String token);

    void logout(User user);

    User register(User user);

    User updateUserData(User user);

    User getUser(Long userId);

    List<User> getAllUsers();

    List<Friend> getUserFriends(Long userId);

    List<Friend> getUserAcceptedFriends(Long userId);

    void setUserFcmToken(User user);

    void releaseUserFcmToken(Long userId);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void addInvite(Long userId, Long roomId);

    List<Friend> getAcceptedFriendsInRoom(Long userId, Long roomId);
}
