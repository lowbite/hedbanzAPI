package com.hedbanz.hedbanzAPI.service.Implementation;

import com.corundumstudio.socketio.BroadcastOperations;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.model.Word;
import com.hedbanz.hedbanzAPI.timer.AfkTimerTask;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.PlayerRepository;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Timer;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;

    @Lookup
    public AfkTimerTask getAfkTimerTask() {
        return null;
    }

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Player> getPlayersFromRoom(Long roomId) {
        return playerRepository.findPlayersByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public Player getPlayer(Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        return player;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setPlayerWord(Word word) {
        if (word.getSenderId() == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (word.getWord() == null){
            throw ExceptionFactory.create(InputError.EMPTY_WORD);
        }
        if(word.getRoomId() == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }

        /*Player wordReceiverPlayer = null;
        List<Player> players = playerRepository.findPlayersByRoomId(word.getRoomId());
        for (Player player : players) {
            if (player.getUser().getUserId().equals(word.getWordReceiverId())) {
                player.setWord(word.getWord());
                wordReceiverPlayer = player;
                break;
            }
        }

        if (wordReceiverPlayer == null) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUser().getUserId().equals(word.getSenderId())) {
                    if (i + 1 < players.size()) {
                        if (players.get(i + 1).getWord() == null) {
                            players.get(i + 1).setWord(word.getWord());
                            wordReceiverPlayer = players.get(i + 1);
                        } else {
                            if (players.get(0).getWord() == null) {
                                players.get(0).setWord(word.getWord());
                                wordReceiverPlayer = players.get(0);
                            }
                        }
                    }
                }
            }
        }
        if (wordReceiverPlayer != null)
            playerRepository.saveAndFlush(wordReceiverPlayer);*/

    Player wordSetter = playerRepository.findPlayerByUser_UserIdAndRoom_Id(word.getSenderId(), word.getRoomId());
        if(wordSetter ==null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
    Player wordReceiver = playerRepository.findPlayerByUser_UserIdAndRoom_Id(wordSetter.getWordSettingUserId(), word.getRoomId());
        if(wordReceiver ==null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        wordReceiver.setWord(word.getWord());
        playerRepository.saveAndFlush(wordReceiver);
}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        if (status == null) {
            throw ExceptionFactory.create(InputError.EMPTY_PLAYERS_STATUS);
        }

        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }
        if (player.getStatus().equals(status)) {
            return player;
        }
        player.setStatus(status);
        return playerRepository.saveAndFlush(player);
    }

    @Transactional
    public Player setPlayerWinner(Long userId, Long roomId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        player.setIsWinner(true);
        player.setAttempt(0);
        return playerRepository.saveAndFlush(player);
    }

    @Transactional
    public void startAfkCountdown(Long userId, Long roomId, BroadcastOperations operations) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        long period = 1000L;

        AfkTimerTask timerTask = getAfkTimerTask();
        timerTask.setUserId(userId);
        timerTask.setRoomId(roomId);
        timerTask.setRoomOperations(operations);
        timerTask.setPeriod(period);
        timerTask.setTimeLeft(60000);
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, period);
    }
}
