package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.SetWordDto;
import org.springframework.core.convert.converter.Converter;

public class MessageToSetWordDtoConverter implements Converter<Message, SetWordDto> {
    private final UserToUserDtoConverter userToUserDtoConverter;

    public MessageToSetWordDtoConverter(UserToUserDtoConverter userToUserDtoConverter) {
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @Override
    public SetWordDto convert(Message message) {
        return new SetWordDto.Builder()
                .setSenderUser(userToUserDtoConverter.convert(message.getSenderUser()))
                .setRoomId(message.getRoom().getId())
                .setType(message.getType().getCode())
                .build();
    }
}
