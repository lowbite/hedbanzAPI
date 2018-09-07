package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, User> {
    @Override
    public User convert(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getId());
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setIconId(userDto.getIconId());
        user.setMoney(userDto.getMoney());
        user.setFcmToken(userDto.getFcmToken());
        user.setGamesNumber(userDto.getGamesNumber());
        return user;
    }
}
