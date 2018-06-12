package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.repository.CrudPlayerRepository;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final CrudPlayerRepository crudPlayerRepository;

    public PlayerServiceImpl(CrudPlayerRepository crudPlayerRepository) {
        this.crudPlayerRepository = crudPlayerRepository;
    }

    @Transactional(readOnly = true)
    public Player getPlayerByUserIdAndRoomId(Long userId, Long roomId) {
        return crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
    }
}
