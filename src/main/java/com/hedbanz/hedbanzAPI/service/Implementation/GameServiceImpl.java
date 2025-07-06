package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.PlayerRepository;
import com.hedbanz.hedbanzAPI.repository.RoomRepository;
import com.hedbanz.hedbanzAPI.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private static final int MAX_GUESS_ATTEMPTS = 3;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    public GameServiceImpl(RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player startGuessing(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.INCORRECT_ROOM_ID);
        List<Player> players = playerRepository.findPlayersByRoomId(roomId);
        if (players == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);
        }
        Player player = playerRepository.findById(players.get(0).getId()).get();
        player.setAttempt(1);
        Room room = roomRepository.findById(roomId).get();
        if (room.getGameStatus() == GameStatus.GUESSING_WORDS)
            throw ExceptionFactory.create(RoomError.GAME_ALREADY_STARTED);
        room.setGameStatus(GameStatus.GUESSING_WORDS);
        roomRepository.saveAndFlush(room);
        playerRepository.saveAndFlush(player);
        return players.get(0);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Player getNextGuessingPlayer(Long roomId, Integer currentAttempt) {
        List<Player> players = playerRepository.findPlayersByRoomIdWithLock(roomId);
        if (players == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);

        Player currentGuessingPlayer = null;
        Player nextGuessingPLayer = null;
        int currentGuessingPlayerIndex = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getAttempt() != 0) {
                currentGuessingPlayer = players.get(i);
                currentGuessingPlayerIndex = i;
            }
        }
        log.info("Current guessing player: " + currentGuessingPlayer);
        if(!currentGuessingPlayer.getAttempt().equals(currentAttempt))
            throw ExceptionFactory.create(RoomError.ALREADY_SENT_NEXT_PLAYER);

        if (currentGuessingPlayer.getAttempt() == -1) {
            if (isLastGuessingPlayer(players, currentGuessingPlayer.getId()))
                return currentGuessingPlayer;
            else
                currentGuessingPlayer.setAttempt(0);
        }
        if (currentGuessingPlayer.getIsWinner()) {
            currentGuessingPlayer.setAttempt(0);
            nextGuessingPLayer = findNonAfkNextGuessingPlayer(currentGuessingPlayerIndex, players);
            if (nextGuessingPLayer == null) {
                nextGuessingPLayer = findAfkNextGuessingPLayer(currentGuessingPlayerIndex, players);
            }
        } else if (currentGuessingPlayer.getAttempt() < MAX_GUESS_ATTEMPTS) {
            if (currentGuessingPlayer.getStatus() == PlayerStatus.ACTIVE) {
                nextGuessingPLayer = currentGuessingPlayer;
                nextGuessingPLayer.setAttempt(nextGuessingPLayer.getAttempt() + 1);
            } else {
                currentGuessingPlayer.setAttempt(0);
                nextGuessingPLayer = findNonAfkNextGuessingPlayer(currentGuessingPlayerIndex, players);
                if (nextGuessingPLayer == null) {
                    nextGuessingPLayer = findAfkNextGuessingPLayer(currentGuessingPlayerIndex, players);
                }
            }
        } else if (currentGuessingPlayer.getAttempt() == MAX_GUESS_ATTEMPTS) {
            currentGuessingPlayer.setAttempt(0);
            nextGuessingPLayer = findNonAfkNextGuessingPlayer(currentGuessingPlayerIndex, players);
            if (nextGuessingPLayer == null) {
                nextGuessingPLayer = findAfkNextGuessingPLayer(currentGuessingPlayerIndex, players);
            }
        }

        if (isLastGuessingPlayer(players, nextGuessingPLayer.getId())) {
            nextGuessingPLayer.setAttempt(-1);
        }
        log.info("Next guessing player: " + nextGuessingPLayer);
        playerRepository.save(nextGuessingPLayer);
        playerRepository.save(currentGuessingPlayer);
        return (Player) nextGuessingPLayer.clone();

    }

    private Player findAfkNextGuessingPLayer(int currentGuessingPlayerIndex, List<Player> players) {
        Player nextPlayer;
        for (int i = currentGuessingPlayerIndex + 1; i < players.size(); i++) {
            nextPlayer = players.get(i);
            if (!nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }

        for (int i = 0; i < currentGuessingPlayerIndex; i++) {
            nextPlayer = players.get(i);
            if (!nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }
        return players.get(currentGuessingPlayerIndex);
    }

    private Player findNonAfkNextGuessingPlayer(int currentGuessingPlayerIndex, List<Player> players) {
        Player nextPlayer;
        for (int i = currentGuessingPlayerIndex + 1; i < players.size(); i++) {
            nextPlayer = players.get(i);
            if (nextPlayer.getStatus() == PlayerStatus.ACTIVE && !nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }

        for (int i = 0; i < currentGuessingPlayerIndex; i++) {
            nextPlayer = players.get(i);
            if (nextPlayer.getStatus() == PlayerStatus.ACTIVE && !nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }
        return null;
    }

    private boolean isLastGuessingPlayer(List<Player> players, long currentPlayerId) {
        for (Player player : players) {
            if (!player.getIsWinner() && player.getId() != currentPlayerId && player.getStatus() != PlayerStatus.LEFT)
                return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isGameOver(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);

        Room room = roomRepository.findById(roomId).orElseThrow(() ->  ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        for (Player player : room.getPlayers()) {
            if (!player.getIsWinner() && player.getStatus() != PlayerStatus.LEFT) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public Room restartGame(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);

        Room room = roomRepository.findById(roomId).orElseThrow(() ->  ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        room.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);

        Player player;
        for (int i = 0; i < room.getPlayers().size(); i++) {
            player = room.getPlayers().get(i);
            if (player.getStatus() == PlayerStatus.LEFT) {
                room.removePlayer(player);
                room.setCurrentPlayersNumber(room.getPlayers().size());
                i--;
            } else {
                player.setIsWinner(false);
                player.setAttempt(0);
                player.setWord(null);
                player.setWordReceiverUserId(null);
            }
        }
        return roomRepository.saveAndFlush(room);
    }

    @Transactional
    public Room setPlayersWordSetters(Long roomId) {
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() ->  ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        if (room.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS
                || !room.getMaxPlayers().equals(room.getPlayers().size())) {
            throw ExceptionFactory.create(RoomError.CANT_START_GAME);
        }

        room.setGameStatus(GameStatus.SETTING_WORDS);

        List<Player> players = room.getPlayers();
        Player player;
        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
            if (i + 1 < players.size()) {
                player.setWordReceiverUserId(players.get(i + 1).getUser().getUserId());
            } else {
                player.setWordReceiverUserId(players.get(0).getUser().getUserId());
            }
        }
        return roomRepository.saveAndFlush(room);
    }

    @Transactional
    public void setGameOverStatus(Long roomId) {
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() ->  ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        room.setGameStatus(GameStatus.GAME_OVER);
        roomRepository.saveAndFlush(room);
    }

    @Transactional
    public void incrementUserGamesNumber(Long roomId, Long userId) {
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }

        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        User user = player.getUser();
        user.setGamesNumber(user.getGamesNumber() + 1);
        playerRepository.save(player);
    }
}
