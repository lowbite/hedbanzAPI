package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.RoomFilter;

import java.util.List;

public interface RoomRepositoryFunctional{
    List<Room> findRoomsByFilter(RoomFilter roomFilter, int page, int size);

    List<Room> findActiveRoomsByFilter(RoomFilter roomFilter, long userId);
}
