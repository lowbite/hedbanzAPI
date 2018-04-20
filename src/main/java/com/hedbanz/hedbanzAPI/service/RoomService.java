package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;

import java.util.List;

public interface RoomService {

    RoomDTO addRoom(RoomDTO roomDTO);

    void deleteRoom(long roomId);

    RoomDTO getRoom(long roomId);

    List<RoomDTO> getAllRooms(int page);

    List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber);

    List<MessageDTO> getAllMessages(long roomId, int pageNumber);

    UserDTO leaveRoom(UserToRoomDTO userToRoomDTO);

    RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO);

    List<Player> getPlayers(long roomId);

    void checkRoomPassword(UserToRoomDTO userToRoomDTO);

    MessageDTO addMessage(MessageDTO messageDTO);

    WordDTO setPlayerWord(WordDTO wordDTO);
}
