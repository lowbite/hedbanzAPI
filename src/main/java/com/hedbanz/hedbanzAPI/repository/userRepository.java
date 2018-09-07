package com.hedbanz.hedbanzAPI.repository;


import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailOrLogin(String email, String login);

    User findUserByLogin(String login);

    User findUserByEmail(String email);

    @Query("SELECT COUNT(f) FROM User u INNER JOIN u.friends f INNER JOIN f.friends ff " +
            "WHERE u.id = :userId AND ff.id = :userId")
    Long countFriends(@Param("userId") long userId);

    List<User> findAllByFcmTokenIsNotNull();

    @Query("SELECT new com.hedbanz.hedbanzAPI.model.Friend(f.id, f.login, f.iconId, 1, 0) FROM User u " +
            "INNER JOIN u.friends f INNER JOIN f.friends ff WHERE u.id = :userId AND ff.id = :userId")
    List<Friend> findAcceptedFriends(@Param("userId") long userId);

    @Query("SELECT new com.hedbanz.hedbanzAPI.model.Friend(f.id, f.login, f.iconId, 1, 0, 0, 1) FROM User u " +
            "INNER JOIN u.friends f INNER JOIN f.friends ff JOIN f.invitedToRooms r " +
            "WHERE u.id = :userId AND ff.id = :userId AND r.id = :roomId")
    List<Friend> findAcceptedFriendsWithInvitesToRoom(@Param("userId") long userId, @Param("roomId") long roomId);

    @Query(value = "SELECT new com.hedbanz.hedbanzAPI.model.Friend(f.id, f.login, f.iconId, 0, 1) FROM User u " +
            "INNER JOIN u.friends f WHERE u.id = :userId")
    List<Friend> findPendingAndAcceptedFriends(@Param("userId") long userId);

    @Query("SELECT new com.hedbanz.hedbanzAPI.model.Friend(u.userId, u.login, u.iconId, 0, 0) FROM  User u " +
            "INNER JOIN u.friends f WHERE f.id = :userId")
    List<Friend> findRequestingFriends(@Param("userId") long userId);

    @Query("SELECT u.fcmToken FROM User u WHERE u.fcmToken <> NULL")
    List<String> findAllFcmTokens();

    @Modifying
    @Query("UPDATE User u SET u.fcmToken = :token WHERE u.id = :user_id")
    int updateUserFcmToken(@Param("token") String token, @Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.fcmToken = null WHERE u.id = :user_id")
    int deleteUserFcmToken(@Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    int updateUserPassword(@Param("userId") long userId, @Param("password") String password);

    @Query("SELECT COUNT(u) FROM User u")
    long findUsersCount();
}
