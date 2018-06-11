package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.FriendDto;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import com.hedbanz.hedbanzAPI.transfer.UserUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User authenticate(User user);

    Optional<UserDetails> findUserByToken(String token);

    void logout(User user);

    User register(User user);

    User updateUserData(User user);

    User getUser(long userId);

    List<FriendDto> getUserFriends(long userId);

    void setUserFcmToken(User user);

    void releaseUserFcmToken(long userId);
}
