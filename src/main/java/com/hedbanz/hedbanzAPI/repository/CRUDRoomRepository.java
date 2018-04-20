package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CRUDRoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>{

    @Query("SELECT new com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers")
    Page<RoomDTO> findAllRooms(Pageable pageable);

    @Query("SELECT new com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO(u.id, u.login, u.imagePath, m.roomId, m.text, m.type, m.createDate) FROM Room r JOIN  r.messages m JOIN  m.senderUser u WHERE r.id = :roomId ORDER BY m.id DESC")
    Page<MessageDTO> findAllMessages(Pageable pageable, @Param("roomId") long roomId);

    @Query("SELECT p FROM Room r JOIN r.players p WHERE r.id = :id")
    List<Player> findPlayers(@Param("id") Long id);
}
