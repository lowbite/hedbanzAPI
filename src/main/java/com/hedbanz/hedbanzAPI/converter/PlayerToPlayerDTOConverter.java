package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.PlayerDTO;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToPlayerDTOConverter implements Converter<Player, PlayerDTO> {
    @Override
    public PlayerDTO convert(Player player) {
        return new PlayerDTO.PlayerDTOBuilder()
                            .setId(player.getId())
                            .setLogin(player.getLogin())
                            .setWord(player.getWord())
                            .setAttempts(player.getAttempts())
                            .setImagePath(player.getImagePath())
                            .setIsAFK(player.getIsAFK())
                            .createPlayerDTO();
    }
}
