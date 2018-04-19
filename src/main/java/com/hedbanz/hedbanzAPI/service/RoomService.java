package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;

import java.util.List;

public interface RoomService {

    RoomDTO addRoom(RoomDTO roomDTO);

    void deleteRoom(long roomId);

    RoomDTO getRoom(long roomId);

    List<RoomDTO> getAllRooms(int page);

    List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber);

    List<Message> getAllMessages(long roomId, int pageNumber);

    UserDTO leaveRoom(UserToRoomDTO userToRoomDTO);

    RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO);

    UserToRoomDTO checkRoomPassword(UserToRoomDTO userToRoomDTO);

    MessageDTO addMessage(MessageDTO messageDTO);

    SetWordDTO setPlayerWord(SetWordDTO setWordDTO);
}
