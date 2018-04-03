package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomFilterDTO;

import java.util.List;

public interface RoomRepositoryFunctional{
    List<RoomDTO> findRoomsByFilter(RoomFilterDTO roomFilterDTO, int page, int size);

}
