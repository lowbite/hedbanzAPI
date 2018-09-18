package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;

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

    public static Player getLastPlayer(List<Player> players) {
        int activePLayers = 0;
        Player onlyOnePlayer = null;
        for (Player player : players) {
            if (player.getStatus() != PlayerStatus.LEFT) {
                if (activePLayers == 1)
                    return null;
                activePLayers++;
                onlyOnePlayer = player;
            }
        }
        return onlyOnePlayer;
    }

    public static boolean isPlayersAbsent(List<Player> players) {
        for (Player player : players) {
            if (player.getStatus() != PlayerStatus.LEFT) {
                return false;
            }
        }
        return true;
    }
}
