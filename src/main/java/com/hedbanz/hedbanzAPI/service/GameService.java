package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.Word;

public interface GameService {

    Player startGuessing(Long roomId);

    Player getNextGuessingPlayer(Long roomId);

    Room setPlayersWordSetters(Long roomId);

    Room setGameOverStatus(Long roomId);

    boolean isGameOver(Long roomId);

    Room restartGame(Long roomId);

    void incrementPlayerGamesNumber(Long roomId, Long userId);
}
