package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomFilterDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserToRoomDTO;
import com.hedbanz.hedbanzAPI.entity.Message;

import java.util.List;

public interface RoomService {

    RoomDTO addRoom(RoomDTO roomDTO);

    void deleteRoom(long roomId);

    RoomDTO getRoom(long roomId);

    List<RoomDTO> getAllRooms(int page);

    List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber);

    RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO);

    void leaveRoom(UserToRoomDTO userToRoomDTO);

    MessageDTO addMessage(MessageDTO messageDTO);

    List<Message> getAllMessages(long roomId, int pageNumber);
}
