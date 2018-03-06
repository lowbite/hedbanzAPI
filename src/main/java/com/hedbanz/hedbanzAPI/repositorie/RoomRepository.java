package com.hedbanz.hedbanzAPI.repositorie;

import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface RoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>{

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findAllRooms(Pageable pageable);

}
