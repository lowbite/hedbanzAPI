package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToPlayerDtoConverter implements Converter<Player, PlayerDto> {
    @Override
    public PlayerDto convert(Player player) {
        return new PlayerDto.PlayerDTOBuilder()
                            .setId(player.getId())
                            .setLogin(player.getLogin())
                            .setWord(player.getWord())
                            .setAttempts(player.getAttempts())
                            .setImagePath(player.getImagePath())
                            .setStatus(player.getStatus().getCode())
                            .createPlayerDTO();
    }
}
