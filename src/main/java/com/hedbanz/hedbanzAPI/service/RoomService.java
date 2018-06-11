package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.*;

import java.util.List;

public interface RoomService {

    RoomDto addRoom(Room room, Long userId);

    void deleteRoom(Long roomId);

    RoomDto getRoom(Long roomId);

    List<RoomDto> getAllRooms(Integer page);

    List<Room> getActiveRooms(Long userId);

    List<RoomDto> getRoomsByFilter(RoomFilterDto roomFilterDto, Integer pageNumber);

    void checkRoomPassword(Long roomId, String password);

    void leaveFromRoom(Long userId, Long roomId);

    RoomDto addUserToRoom(Long userId, Long roomId, String password);

    PlayerDto setPlayerStatus(Long userId, Long roomId, PlayerStatus status);

    List<PlayerDto> getPlayers(Long roomId);

    Boolean startGame(Long roomId);

    void setPlayerWord(WordDto wordDto);

    PlayerDto startGuessing(Long roomId);

    PlayerDto nextGuessing(Long roomId);

    void checkPlayerInRoom(Long userId, Long roomId);


}
