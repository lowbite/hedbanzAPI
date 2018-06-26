package com.hedbanz.hedbanzAPI.service;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.hedbanz.hedbanzAPI.entity.Player;

public interface PlayerService {
    Player getPlayerByUserIdAndRoomId(Long userId, Long roomId);

    Player setPlayerWinner(Long userId, Long roomId);

    void startAfkCountdown(Long userId, Long roomId, BroadcastOperations operations);
}
