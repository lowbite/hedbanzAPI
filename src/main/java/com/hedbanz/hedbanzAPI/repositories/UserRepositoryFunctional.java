package com.hedbanz.hedbanzAPI.repositories;

import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepositoryFunctional {
    @Query("SELECT u FROM User u WHERE u.login = :login")
    User findUserByLogin(@Param("login") String login);


    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.login = :login, u.password = :newPassword WHERE u.id = :userId AND u.password LIKE :oldPassword")
    int updateUserData(@Param("userId") long userId,@Param("login") String login, @Param("newPassword") String newPassword, @Param("oldPassword") String oldPassword);

}
