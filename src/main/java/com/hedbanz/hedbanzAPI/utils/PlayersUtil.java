package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Player;

import java.util.List;

public class PlayersUtil {

    public static int getActivePlayersNumber(List<Player> players) {
        int activePlayersNumber = 0;
        for (Player player: players) {
            if(player.getStatus() != PlayerStatus.LEFT)
                activePlayersNumber++;
        }
        return activePlayersNumber;
    }
}
