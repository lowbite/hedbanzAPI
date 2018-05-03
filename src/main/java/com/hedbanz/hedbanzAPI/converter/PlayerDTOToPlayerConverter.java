package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.PlayerDTO;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerDTOToPlayerConverter implements Converter<PlayerDTO, Player> {
    @Override
    public Player convert(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setId(player.getId());
        player.setLogin(player.getLogin());
        player.setWord(player.getWord());
        player.setAttempts(player.getAttempts());
        player.setImagePath(player.getImagePath());
        player.setIsAFK(player.getIsAFK());
        return player;
    }
}
