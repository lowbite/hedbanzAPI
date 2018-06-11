package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoomDtoToRoomConverter implements Converter<RoomDto, Room> {
    public RoomDtoToRoomConverter(){

    }

    @Override
    public Room convert(RoomDto roomDto) {
        Room room = new Room();
        room.setId(roomDto.getId());
        room.setName(roomDto.getName());
        room.setPassword(roomDto.getPassword());
        room.setMaxPlayers(roomDto.getMaxPlayers());
        room.setIsPrivate(roomDto.getIsPrivate());
        return room;
    }
}
