package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;

import javax.persistence.OrderBy;
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

    @Query("SELECT p FROM Player p JOIN p.user u JOIN p.room r WHERE u.id = :userId AND r.id = :roomId")
    Player findPlayerByUserIdAndRoomId(@Param("userId") long userId, @Param("roomId") long roomId);
}
