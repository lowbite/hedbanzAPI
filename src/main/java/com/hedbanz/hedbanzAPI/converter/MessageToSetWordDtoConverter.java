package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.SetWordDto;
import org.springframework.core.convert.converter.Converter;

public class MessageToSetWordDtoConverter implements Converter<Message, SetWordDto> {
    @Override
    public SetWordDto convert(Message message) {
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        return new SetWordDto.Builder()
                .setSenderUser(toUserDtoConverter.convert(message.getSenderUser()))
                .setRoomId(message.getRoom().getId())
                .setType(message.getType().getCode())
                .build();
    }
}
