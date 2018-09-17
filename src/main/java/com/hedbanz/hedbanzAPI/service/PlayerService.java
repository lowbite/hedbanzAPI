package com.hedbanz.hedbanzAPI.service;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.Word;

import java.util.List;

public interface PlayerService {
    List<Player> getPlayersFromRoom(Long roomId);

    Player getPlayer(Long userId, Long roomId);

    void setPlayerWord(Word word);

    Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status);

    Player setPlayerWinner(Long userId, Long roomId);
}
