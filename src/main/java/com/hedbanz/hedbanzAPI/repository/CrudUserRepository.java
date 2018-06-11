package com.hedbanz.hedbanzAPI.repository;


import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.FriendDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrudUserRepository extends JpaRepository<User, Long> {

    User findUserByLogin(String login);

    User findUserByEmail(String email);

    User findUserBySecurityToken(String securityToken);

    @Query(value = "SELECT new com.hedbanz.hedbanzAPI.transfer.FriendDto(f.id, f.login, f.imagePath, 1) FROM User u " +
            "INNER JOIN u.friends f INNER JOIN f.friends ff WHERE u.id = :userId AND ff.id = :userId")
    List<FriendDto>  getAcceptedFriends(@Param("userId") long userId);

    @Query(value = "SELECT new com.hedbanz.hedbanzAPI.transfer.FriendDto(f.id, f.login, f.imagePath) FROM User u " +
            "INNER JOIN u.friends f WHERE u.id = :userId")
    List<FriendDto> getAllFriends(@Param("userId") long userId);

    @Modifying
    @Query("UPDATE User u SET u.fcmToken = :token WHERE u.id = :user_id")
    int updateUserFcmToken(@Param("token") String token, @Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.fcmToken = null WHERE u.id = :user_id")
    int deleteUserFcmToken(@Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.securityToken = :token WHERE u.id = :user_id")
    int updateUserToken(@Param("token") String token, @Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.securityToken = null WHERE u.id = :user_id")
    int deleteUserToken(@Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.login = :login, u.password = :newPassword WHERE u.id = :userId")
    int updateUserData(@Param("userId") long userId,@Param("login") String login, @Param("newPassword") String newPassword);
}
