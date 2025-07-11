package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Player;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long>, PagingAndSortingRepository<Player, Long> {
    @Modifying
    @Query("UPDATE Player p SET p.attempt = :attempt WHERE p.id = :id")
    int updatePlayerAttempts(@Param("attempt") int attempt, @Param("id") long id);

    @Query("SELECT p FROM Player p JOIN p.user u WHERE u.id = :userId")
    List<Player> findPlayersByUserId(@Param("userId") long userId);

    @Query("SELECT p FROM Player p JOIN p.room r WHERE r.id = :roomId")
    @OrderBy("id")
    List<Player> findPlayersByRoomId(@Param("roomId") long roomId);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM Player p JOIN p.room r WHERE r.id = :roomId")
    @OrderBy("id")
    List<Player> findPlayersByRoomIdWithLock(@Param("roomId") long roomId);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM Player p JOIN p.room r JOIN p.user u WHERE r.id = :roomId AND u.userId = :userId")
    Player findPlayerByUser_UserIdAndRoom_IdWithLock(@Param("userId") long userId, @Param("roomId") long roomId);

    Player findPlayerByUser_UserIdAndRoom_Id(@Param("userId") long userId, @Param("roomId") long roomId);
}
