package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.UpdateUserData;
import com.hedbanz.hedbanzAPI.entity.User;

public interface UserService {

    User authenticate(User user);

    User register(User user);

    User updateUserData(UpdateUserData userData);

    User getUser(long userId);
}
