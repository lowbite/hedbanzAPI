package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.PasswordResetKeyWord;
import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetKeyWordRepository extends JpaRepository<PasswordResetKeyWord, Long>{

    PasswordResetKeyWord findByKeyWord(String keyWord);

    @Query("SELECT u FROM PasswordResetKeyWord k JOIN k.user u WHERE k.keyWord = :keyWord")
    User findUserByKeyWord(@Param("keyWord") String keyWord);

    PasswordResetKeyWord findByUser_login(String login);

    int deleteByUser_login(String login);
}
