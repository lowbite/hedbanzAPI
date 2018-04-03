package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.User;

import java.util.List;

public interface UserRepositoryFunctional {
    List<User> getFriends();
}
