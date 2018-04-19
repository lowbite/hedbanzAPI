package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.core.convert.converter.Converter;

public class UserToPlayerConverter implements Converter<User, Player> {
    public Player convert(User user) {
        return Player.newBuilder()
                .setId(user.getId())
                .setLogin(user.getLogin())
                .setImagePath(user.getImagePath())
                .build();
    }
}
