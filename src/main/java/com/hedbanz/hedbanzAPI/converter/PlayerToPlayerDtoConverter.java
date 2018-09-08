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
                .setIconId(player.getUser().getIconId())
                .setWord(player.getWord())
                .setWordSettingUserId(player.getWordReceiverUserId())
                .setAttempt(player.getAttempt())
                .setIconId(player.getUser().getIconId())
                .setStatus(player.getStatus().getCode())
                .setUserId(player.getUser().getUserId())
                .setIsWinner(player.getIsWinner())
                .createPlayerDTO();
    }
}
