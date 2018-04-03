package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        room.setUsers(roomDTO.getUsers().stream()
                .map(userDTO -> convertUser(userDTO))
                .collect(Collectors.toSet()));
        room.setIsPrivate(roomDTO.getIsPrivate());
        return room;
    }

    private User convertUser(UserDTO userD) {
        User userDTO = new User();
        userDTO.setId(userD.getId());
        userDTO.setLogin(userD.getLogin());
        userDTO.setPassword(userD.getPassword());
        userDTO.setEmail(userD.getEmail());
        userDTO.setImagePath(userD.getImagePath());
        userDTO.setMoney(userD.getMoney());
        userDTO.setRegistrationDate(userD.getRegistrationDate());
        return userDTO;
    }
}
