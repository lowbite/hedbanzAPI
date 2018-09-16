package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;

public class MessageToMessageDtoConverter implements Converter<Message, MessageDto> {
    private final UserToUserDtoConverter userToUserDtoConverter;

    public MessageToMessageDtoConverter(UserToUserDtoConverter userToUserDtoConverter) {
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @Override
    public MessageDto convert(Message message) {
        return new MessageDto.MessageDTOBuilder()
                .setSenderUser(message.getSenderUser() != null ? userToUserDtoConverter.convert(message.getSenderUser()) : null)
                .setRoomId(message.getRoom().getId())
                .setText(message.getText())
                .setType(message.getType().getCode())
                .setCreateDate(new Timestamp(message.getCreatedAt().getTime()))
                .createMessageDTO();
    }
}
