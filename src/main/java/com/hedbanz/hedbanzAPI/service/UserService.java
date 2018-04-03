package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserDTO authenticate(UserDTO userDDTO);

    UserDTO register(UserDTO userDDTO);

    UserDTO updateUserData(UserUpdateDTO userData);

    UserDTO getUser(long userId);

    List<FriendDTO> getUserFriends(long userId);

    void setUserToken(long userId, String token);

    void releaseUserToken(long userId);
}
