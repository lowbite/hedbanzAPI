package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerDtoToPlayerConverter implements Converter<PlayerDto, Player> {
    @Override
    public Player convert(PlayerDto playerDto) {
        Player player = new Player();
        player.setId(playerDto.getId());
        player.setLogin(playerDto.getLogin());
        player.setWord(playerDto.getWord());
        player.setAttempts(playerDto.getAttempts());
        player.setImagePath(playerDto.getImagePath());
        return player;
    }
}
