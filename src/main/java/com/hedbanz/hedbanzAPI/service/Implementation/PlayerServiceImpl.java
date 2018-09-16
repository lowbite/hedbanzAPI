package com.hedbanz.hedbanzAPI.service.Implementation;

import com.corundumstudio.socketio.BroadcastOperations;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.model.Word;
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

    Player wordSetter = playerRepository.findPlayerByUser_UserIdAndRoom_Id(word.getSenderId(), word.getRoomId());
        if(wordSetter ==null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
    Player wordReceiver = playerRepository.findPlayerByUser_UserIdAndRoom_Id(wordSetter.getWordReceiverUserId(), word.getRoomId());
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
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_IdWithLock(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        if(player.getIsWinner())
            throw ExceptionFactory.create(UserError.ALREADY_WIN);
        player.setIsWinner(true);
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Integer getActivePlayersNumber(List<Player> players) {
        int activePlayersNumber = 0;
        for (Player player: players) {
            if(player.getStatus() != PlayerStatus.LEFT)
                activePlayersNumber++;
        }
        return activePlayersNumber;
    }
}
