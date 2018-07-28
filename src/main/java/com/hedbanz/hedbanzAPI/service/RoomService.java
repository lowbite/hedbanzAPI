package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.RoomFilter;
import com.hedbanz.hedbanzAPI.model.Word;

import java.util.List;

public interface RoomService {

    Room addRoom(Room room, Long userId);

    void deleteRoom(Long roomId);

    Room getRoom(Long roomId);

    List<Room> getAllRooms(Integer page);

    List<Room> getActiveRooms(Long userId);

    List<Room> getRoomsByFilter(RoomFilter roomFilter, Integer pageNumber);

    List<Room> getActiveRoomsByFilter(RoomFilter roomFilter, Long userId);

    void checkRoomPassword(Long roomId, String password);

    Room leaveFromRoom(Long userId, Long roomId);

    Room addUserToRoom(Long userId, Long roomId, String password);

    Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status);

    Boolean startGame(Long roomId);

    void setPlayerWord(Word word);

    Player startGuessing(Long roomId);

    Player getNextGuessingPlayer(Long roomId);

    void checkPlayerInRoom(Long userId, Long roomId);

    Room setPlayersWordSetters(Long roomId);

    Room setGameOverStatus(Long roomId);

    boolean isGameOver(Long roomId);

    Room restartGame(Long roomId);
}
