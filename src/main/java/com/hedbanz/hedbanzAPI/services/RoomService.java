package com.hedbanz.hedbanzAPI.services;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.RoomFilter;
import com.hedbanz.hedbanzAPI.entity.UserToRoom;

import java.util.List;

public interface RoomService {

    Room addRoom(Room room);

    List<Room> getAllRooms(int page);

    List<Room> getRoomsByFilter(RoomFilter roomFilter, int pageNumber);

    Room addUserToRoom(UserToRoom userToRoom);

    void leaveRoom(UserToRoom userToRoom);
}
