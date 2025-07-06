package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Room findRoomByName(String name);

    @Query("SELECT r FROM Room r WHERE r.currentPlayersNumber < r.maxPlayers AND r.gameStatus = com.hedbanz.hedbanzAPI.constant.GameStatus.WAITING_FOR_PLAYERS")
    Page<Room> findAllRooms(Pageable pageable);

    @Query("SELECT r FROM Room r JOIN r.players p JOIN p.user u WHERE u.id = :userId")
    List<Room> findActiveRooms(@Param("userId") long userId);

    @Query("SELECT COUNT(r) FROM Room r")
    Long findRoomsCountByFilter(Specification<Room> specification);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Room> findRoomById(Long id);
}
