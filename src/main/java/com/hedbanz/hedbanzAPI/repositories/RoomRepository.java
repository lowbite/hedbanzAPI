package com.hedbanz.hedbanzAPI.repositories;

import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface RoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>, RoomRepositoryFunctional {

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findAllRooms(Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomById(@Param("room_id") long roomId, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByIdWithMaxPlayers(@Param("room_id") long roomId, @Param("max_players") int maxPlayers, @Param("min_players") int minPlayers, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.password LIKE '' AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByIdWithoutPassword(@Param("room_id") long roomId, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.password NOT LIKE '' AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByIdWithPassword(@Param("room_id") long roomId, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.password LIKE '' AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByIdWithoutPasswordWithMaxPlayers(@Param("room_id") long roomId, @Param("max_players") int maxPlayers, @Param("min_players") int minPlayers, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.id = :room_id AND r.password NOT LIKE '' AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByIdWithPasswordWithMaxPlayers(@Param("room_id") long roomId, @Param("max_players") int maxPlayers,  @Param("min_players") int minPlayers, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name% AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByName(@Param("room_name") String roomName, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name% AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByNameWithMaxPlayers(@Param("room_name") String roomName, @Param("max_players") int maxPlayers, @Param("min_players") int minPlayers, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name AND r.password LIKE '' AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByNameWithoutPassword(@Param("room_name") String roomName, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name AND r.password NOT LIKE '' AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByNameWithPassword(@Param("room_name") String roomName, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name AND r.password LIKE '' AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players  AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByNameWithoutPasswordWithMaxPlayers(@Param("room_name") String roomName, @Param("max_players") int maxPlayers, @Param("min_players") int minPlayers, Pageable pageable);

    @Query("SELECT new Room(r.id, r.name, r.password, r.maxPlayers, r.currentPlayersNumber) FROM Room r WHERE r.name LIKE :room_name AND r.password NOT LIKE '' AND r.maxPlayers <= :max_players AND r.maxPlayers >= :min_players AND r.currentPlayersNumber < r.maxPlayers")
    Page<Room> findRoomByNameWithPasswordWithMaxPlayers(@Param("room_name") String roomName, @Param("max_players") int maxPlayers,  @Param("min_players") int minPlayers, Pageable pageable);
}
