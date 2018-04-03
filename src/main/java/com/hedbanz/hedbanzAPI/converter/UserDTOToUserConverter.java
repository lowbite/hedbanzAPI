package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDTOToUserConverter implements Converter<UserDTO, User> {
    @Override
    public User convert(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setImagePath(userDTO.getImagePath());
        user.setMoney(userDTO.getMoney());
        user.setRegistrationDate(userDTO.getRegistrationDate());
        return user;
    }
}
