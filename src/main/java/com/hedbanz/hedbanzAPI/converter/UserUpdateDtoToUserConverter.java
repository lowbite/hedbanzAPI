package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.UserUpdateDto;
import org.apache.http.util.TextUtils;
import org.springframework.core.convert.converter.Converter;

public class UserUpdateDtoToUserConverter implements Converter<UserUpdateDto, User> {

    @Override
    public User convert(UserUpdateDto userUpdateDto) {
        return User.UserBuilder().setId(userUpdateDto.getId())
                                .setLogin(userUpdateDto.getLogin())
                                .setPassword(TextUtils.isEmpty(userUpdateDto.getNewPassword()) ? userUpdateDto.getNewPassword() : userUpdateDto.getOldPassword())
                                .build();
    }
}
