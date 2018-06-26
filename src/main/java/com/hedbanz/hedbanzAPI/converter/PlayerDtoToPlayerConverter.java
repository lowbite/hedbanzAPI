package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerDtoToPlayerConverter implements Converter<PlayerDto, Player> {
    @Override
    public Player convert(PlayerDto playerDto) {
        Player player = new Player();
        player.setId(playerDto.getId());
        player.setWord(playerDto.getWord());
        player.setAttempt(playerDto.getAttempt());
        return player;
    }
}
