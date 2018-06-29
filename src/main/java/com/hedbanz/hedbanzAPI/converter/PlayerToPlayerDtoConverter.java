package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToPlayerDtoConverter implements Converter<Player, PlayerDto> {
    @Override
    public PlayerDto convert(Player player) {
        return new PlayerDto.PlayerDTOBuilder()
                .setId(player.getId())
                .setLogin(player.getUser().getLogin())
                .setWord(player.getWord())
                .setAttempt(player.getAttempt())
                .setImagePath(player.getUser().getImagePath())
                .setStatus(player.getStatus().getCode())
                .setUserId(player.getUser().getId())
                .setIsWinner(player.getIsWinner())
                .createPlayerDTO();
    }
}
