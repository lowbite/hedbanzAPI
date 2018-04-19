package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.core.convert.converter.Converter;

public class PlayerToUserDTOConverter implements Converter<Player, UserDTO> {
    @Override
    public UserDTO convert(Player player) {
        return new UserDTO.UserDTOBuilder()
                .setId(player.getId())
                .setLogin(player.getLogin())
                .setImagePath(player.getImagePath())
                .createUserDTO();
    }
}
