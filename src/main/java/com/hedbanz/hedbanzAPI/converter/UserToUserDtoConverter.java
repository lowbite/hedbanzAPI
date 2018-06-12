package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        return new UserDto.UserDTOBuilder()
                    .setId(user.getId())
                    .setLogin(user.getLogin())
                    .setEmail(user.getEmail())
                    .setImagePath(user.getImagePath())
                    .setMoney(user.getMoney())
                    .setRegistrationDate(user.getRegistrationDate())
                    .setToken(user.getSecurityToken())
                    .setFcmToken(user.getFcmToken())
                    .build();
    }
}
