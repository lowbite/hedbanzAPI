package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        roomDTO.setUsers(room.getUsers().stream()
                .map(user -> convertUserDTO(user))
                .collect(Collectors.toSet()));
        roomDTO.setIsPrivate(room.getIsPrivate());
        return roomDTO;
    }


    private Set<UserDTO> convertUserDTOSToUsers(Set<User>userDTOS){
        Set<UserDTO> userDS = new HashSet<>();
        for (User userDTO: userDTOS) {
            userDS.add(convertUserDTO(userDTO));
        }
        return userDS;
    }


    private UserDTO convertUserDTO(User userDTO) {
        UserDTO userD = new UserDTO();
        userD.setId(userDTO.getId());
        userD.setLogin(userDTO.getLogin());
        userD.setEmail(userDTO.getEmail());
        userD.setImagePath(userDTO.getImagePath());
        userD.setMoney(userDTO.getMoney());
        userD.setRegistrationDate(userD.getRegistrationDate());
        return userD;
    }
}
