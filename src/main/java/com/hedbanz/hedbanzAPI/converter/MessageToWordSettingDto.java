package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import com.hedbanz.hedbanzAPI.transfer.WordSettingDto;
import org.springframework.core.convert.converter.Converter;

public class MessageToWordSettingDto implements Converter<Message, WordSettingDto> {
    @Override
    public WordSettingDto convert(Message message) {
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        return new WordSettingDto.Builder()
                .setSenderUser(toUserDtoConverter.convert(message.getSenderUser()))
                .setRoomId(message.getRoom().getId())
                .setType(message.getType().getCode())
                .build();
    }
}
