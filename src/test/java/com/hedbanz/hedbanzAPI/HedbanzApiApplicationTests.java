package com.hedbanz.hedbanzAPI;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

@SpringBootTest
public class HedbanzApiApplicationTests{
    @Test
    public void contextLoads() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            players.add(new Player());
        }
        long time = System.currentTimeMillis();
        for (Player player: players) {
            player.setId(1);
        }
        //players.forEach(player -> {player.setId(1);});
        System.out.println(System.currentTimeMillis() - time);
    }
}

class Player{
    long id;

    public Player(){}

    public void setId(long id) {
        this.id = id;
    }
}

