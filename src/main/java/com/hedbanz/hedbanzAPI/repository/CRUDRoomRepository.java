package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CRUDRoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>{

    @Query("SELECT new com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers")
    Page<RoomDTO> findAllRooms(Pageable pageable);

    @Query("SELECT m FROM Room r JOIN r.messages m WHERE r.id = :roomId")
    Page<Message> findAllMessages(Pageable pageable, @Param("roomId") long roomId);
}
