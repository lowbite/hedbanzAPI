package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomDtoToRoomConverter implements Converter<RoomDto, Room> {
    public RoomDtoToRoomConverter(){

    }

    @Override
    public Room convert(RoomDto roomDto) {
        return new Room.Builder()
                .setId(roomDto.getId())
                .setName(roomDto.getName())
                .setPassword(roomDto.getPassword())
                .setMaxPlayers(roomDto.getMaxPlayers())
                .setIsPrivate(roomDto.getIsPrivate())
                .setStickerId(roomDto.getStickerId())
                .setIconId(roomDto.getIconId())
                .build();
    }
}
