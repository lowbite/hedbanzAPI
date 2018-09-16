package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.RoomFilter;

import java.util.List;

public interface RoomService {

    Room addRoom(Room room, Long userId);

    void deleteRoom(Long roomId);

    Room getRoom(Long roomId);

    List<Room> getAllRooms(Integer page);

    List<Room> getActiveRooms(Long userId);

    List<Room> getRoomsByFilter(RoomFilter roomFilter, Integer pageNumber);

    List<Room> getActiveRoomsByFilter(RoomFilter roomFilter, Long userId);

    void checkRoomPassword(Long roomId, String password);

    void leaveUserFromRoom(Long userId, Long roomId);

    void addUserToRoom(Long userId, Long roomId, String password);

    void checkPlayerInRoom(Long userId, Long roomId);

    Long getRoomsCountByAdminFilter(RoomFilter roomFilter);
}
