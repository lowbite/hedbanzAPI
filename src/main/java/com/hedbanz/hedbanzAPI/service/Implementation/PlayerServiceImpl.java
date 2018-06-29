package com.hedbanz.hedbanzAPI.service.Implementation;

import com.corundumstudio.socketio.BroadcastOperations;
import com.hedbanz.hedbanzAPI.AfkTimerTask;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CrudPlayerRepository;
import com.hedbanz.hedbanzAPI.repository.CrudRoomRepository;
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
    public int i;
    private final CrudPlayerRepository crudPlayerRepository;
    private final CrudRoomRepository crudRoomRepository;

    @Lookup
    public AfkTimerTask getAfkTimerTask() {
        return null;
    }

    @Autowired
    public PlayerServiceImpl(CrudPlayerRepository crudPlayerRepository, CrudRoomRepository crudRoomRepository) {
        this.crudPlayerRepository = crudPlayerRepository;
        this.crudRoomRepository = crudRoomRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Player> getPlayers(Long roomId) {
        return crudPlayerRepository.findPlayersByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public Player getPlayerByUserIdAndRoomId(Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        return player;
    }

    @Transactional
    public Player setPlayerWinner(Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        player.setIsWinner(true);
        return crudPlayerRepository.saveAndFlush(player);
    }

    @Transactional
    public void startAfkCountdown(Long userId, Long roomId, BroadcastOperations operations) {
        if (userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        long period = 5000l;

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
