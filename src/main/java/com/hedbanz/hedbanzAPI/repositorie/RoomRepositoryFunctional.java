package com.hedbanz.hedbanzAPI.repositorie;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.RoomFilter;

import java.util.List;

public interface RoomRepositoryFunctional{
    List<Room> findRoomsByFilter(RoomFilter roomFilter, int page, int size);
}
