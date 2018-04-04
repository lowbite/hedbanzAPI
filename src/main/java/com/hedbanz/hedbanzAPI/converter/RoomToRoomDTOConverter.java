package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomToRoomDTOConverter implements Converter<Room, RoomDTO> {
    public RoomToRoomDTOConverter(){

    }

    @Override
    public RoomDTO convert(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setCurrentPlayersNumber(room.getCurrentPlayersNumber());
        roomDTO.setMaxPlayers(room.getMaxPlayers());
        roomDTO.setIsPrivate(room.getIsPrivate());
        return roomDTO;
    }
}
