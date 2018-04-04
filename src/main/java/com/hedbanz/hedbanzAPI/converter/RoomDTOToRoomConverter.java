package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomDTOToRoomConverter implements Converter<RoomDTO, Room> {
    public RoomDTOToRoomConverter(){

    }

    @Override
    public Room convert(RoomDTO roomDTO) {
        Room room = new Room();
        room.setId(roomDTO.getId());
        room.setName(roomDTO.getName());
        room.setPassword(roomDTO.getPassword());
        room.setCurrentPlayersNumber(roomDTO.getCurrentPlayersNumber());
        room.setMaxPlayers(roomDTO.getMaxPlayers());
        room.setIsPrivate(roomDTO.getIsPrivate());
        return room;
    }
}
