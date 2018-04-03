package com.hedbanz.hedbanzAPI.repository;


import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CRUDUserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.login LIKE :login")
    User findUserByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.email LIKE :email")
    User findUserByEmail(@Param("email") String email);

    @Query(value = "SELECT new com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO(f.id, f.login, f.imagePath, 1) FROM User u " +
            "INNER JOIN u.friends f INNER JOIN f.friends ff WHERE u.id = :userId AND ff.id = :userId")
    List<FriendDTO>  getAcceptedFriends(@Param("userId") long userId);

    @Query(value = "SELECT new com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO(f.id, f.login, f.imagePath) FROM User u " +
            "INNER JOIN u.friends f WHERE u.id = :userId")
    List<FriendDTO> getAllFriends(@Param("userId") long userId);

    @Modifying
    @Query("UPDATE User u SET u.token = :token WHERE u.id = :user_id")
    int updateUserToken(@Param("token") String token, @Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.token = null WHERE u.id = :user_id")
    int deleteUserToken(@Param("user_id") long userId);

    @Modifying
    @Query("UPDATE User u SET u.login = :login, u.password = :newPassword WHERE u.id = :userId")
    int updateUserData(@Param("userId") long userId,@Param("login") String login, @Param("newPassword") String newPassword);
}
