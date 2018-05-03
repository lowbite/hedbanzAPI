package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.*;

import java.util.List;

public interface RoomService {

    RoomDTO addRoom(RoomDTO roomDTO);

    void deleteRoom(long roomId);

    RoomDTO getRoom(long roomId);

    List<RoomDTO> getAllRooms(int page);

    List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber);

    void checkRoomPassword(UserToRoomDTO userToRoomDTO);

    void leaveRoom(UserToRoomDTO userToRoomDTO);

    RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO);

    PlayerDTO setPlayerAFK(UserToRoomDTO userToRoom, Boolean isAFK);

    List<PlayerDTO> getPlayers(long roomId);

    Boolean startGame(long roomId);

    void setPlayerWord(WordDTO wordDTO);

    PlayerDTO startGuessing(long roomId);

    PlayerDTO nextGuessing(long roomId);

    void checkPlayerInRoom(UserToRoomDTO userToRoomDTO);


}
