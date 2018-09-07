package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        return new UserDto.Builder()
                    .setId(user.getUserId())
                    .setLogin(user.getLogin())
                    .setEmail(user.getEmail())
                    .setIconId(user.getIconId())
                    .setMoney(user.getMoney())
                    .setRegistrationDate(new Timestamp(user.getCreatedAt().getTime()))
                    .setGamesNumber(user.getGamesNumber())
                    .build();
    }
}
