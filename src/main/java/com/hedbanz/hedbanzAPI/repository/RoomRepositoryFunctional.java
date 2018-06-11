package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import com.hedbanz.hedbanzAPI.transfer.RoomFilterDto;

import java.util.List;

public interface RoomRepositoryFunctional{
    List<RoomDto> findRoomsByFilter(RoomFilterDto roomFilterDto, int page, int size);

}
