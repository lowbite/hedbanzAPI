package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;

public class MessageToMessageDtoConverter implements Converter<Message, MessageDto> {
    @Override
    public MessageDto convert(Message message) {
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        return new MessageDto.MessageDTOBuilder()
                .setSenderUser(message.getSenderUser() != null ? toUserDtoConverter.convert(message.getSenderUser()) : null)
                .setRoomId(message.getRoom().getId())
                .setText(message.getText())
                .setType(message.getType().getCode())
                .setCreateDate(new Timestamp(message.getCreatedAt().getTime()))
                .createMessageDTO();
    }
}
