package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDTOConverter implements Converter<User, UserDTO> {
    @Override
    public UserDTO convert(User user) {
        UserDTO userDTO = new UserDTO.UserDTOBuilder().createUserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        userDTO.setEmail(user.getEmail());
        userDTO.setImagePath(user.getImagePath());
        userDTO.setMoney(user.getMoney());
        userDTO.setRegistrationDate(user.getRegistrationDate());
        return userDTO;
    }
}
