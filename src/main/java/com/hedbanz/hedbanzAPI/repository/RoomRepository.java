package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Room findRoomByName(String name);

    @Query("SELECT r FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers AND r.gameStatus = com.hedbanz.hedbanzAPI.constant.GameStatus.WAITING_FOR_PLAYERS")
    Page<Room> findAllRooms(Pageable pageable);

    @Query("SELECT r FROM Room r JOIN r.players p JOIN p.user u WHERE u.id = :userId")
    List<Room> findActiveRooms(@Param("userId") long userId);

    @Query("SELECT new com.hedbanz.hedbanzAPI.model.Friend(u.id, u.login, u.iconId, 1, 0, 1, 0) FROM Room r INNER JOIN r.players p INNER JOIN p.user u " +
            "INNER JOIN u.friends f INNER JOIN f.friends ff WHERE f.id = :userId AND ff.id = u.id AND r.id = :roomId")
    List<Friend> findAcceptedFriendsInRoom(@Param("userId") long userId, @Param("roomId") long roomId);

    @Query("SELECT COUNT(r) FROM Room r")
    Long findRoomsCountByFilter(Specification<Room> specification);

    Room findById(Long id);
}
