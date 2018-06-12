package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Player;

public interface PlayerService {
    Player getPlayerByUserIdAndRoomId(Long userId, Long roomId);
}
