package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;

public interface GameService {

    Player startGuessing(Long roomId);

    Player getNextGuessingPlayer(Long roomId, Integer currentAttempt);

    Room setPlayersWordSetters(Long roomId);

    void setGameOverStatus(Long roomId);

    boolean isGameOver(Long roomId);

    Room restartGame(Long roomId);

    void incrementUserGamesNumber(Long roomId, Long userId);
}
