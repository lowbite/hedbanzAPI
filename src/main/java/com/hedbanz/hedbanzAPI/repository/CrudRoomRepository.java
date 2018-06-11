package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrudRoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>{

    @Query("SELECT new com.hedbanz.hedbanzAPI.transfer.RoomDto(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers AND r.gameStatus = com.hedbanz.hedbanzAPI.constant.GameStatus.WAITING_FOR_PLAYERS")
    Page<RoomDto> findAllRooms(Pageable pageable);


    @Query("SELECT new com.hedbanz.hedbanzAPI.transfer.PlayerDto(p.id, p.login, p.imagePath, p.word, p.attempts, p.status) FROM Room r JOIN r.players p WHERE r.id = :id ORDER BY p.id ASC")
    List<PlayerDto> findPlayers(@Param("id") Long id);
}
