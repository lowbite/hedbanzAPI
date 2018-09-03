package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
                    .setRegistrationDate(user.getRegistrationDate())
                    .setGamesNumber(user.getGamesNumber())
                    .build();
    }
}
