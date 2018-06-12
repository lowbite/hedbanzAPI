package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.UserDto;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToUserDtoConverter implements Converter<Player, UserDto> {
    @Override
    public UserDto convert(Player player) {
        return new UserDto.UserDTOBuilder()
                .setId(player.getId())
                .setLogin(player.getUser().getLogin())
                .setImagePath(player.getUser().getImagePath())
                .build();
    }
}
