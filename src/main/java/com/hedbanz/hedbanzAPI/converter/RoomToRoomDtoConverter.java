package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import com.hedbanz.hedbanzAPI.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomToRoomDtoConverter implements Converter<Room, RoomDto> {

    @Override
    public RoomDto convert(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setName(room.getName());
        roomDto.setCurrentPlayersNumber(room.getCurrentPlayersNumber());
        roomDto.setMaxPlayers(room.getMaxPlayers());
        roomDto.setIsPrivate(room.getIsPrivate());
        roomDto.setStickerId(room.getStickerId());
        roomDto.setIconId(room.getIconId());
        roomDto.setGameStatus(room.getGameStatus().getCode());
        return roomDto;
    }
}
