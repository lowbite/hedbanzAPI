package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.Friend;
import org.springframework.core.convert.converter.Converter;

public class UserToFriendConverter implements Converter<User, Friend> {
    @Override
    public Friend convert(User user) {
        Friend friend = new Friend();
        friend.setId(user.getUserId());
        friend.setIconId(user.getIconId());
        friend.setLogin(user.getLogin());
        return friend;
    }
}
