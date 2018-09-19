package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.utils.MessageUtil;
import org.springframework.core.convert.converter.Converter;

public class MessageDtoToMessageConverter implements Converter<MessageDto, Message> {
    private final UserDtoToUserConverter userDtoToUserConverter;

    public MessageDtoToMessageConverter(UserDtoToUserConverter userDtoToUserConverter) {
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @Override
    public Message convert(MessageDto messageDto) {
        return Message.Builder()
                .setSenderUser(userDtoToUserConverter.convert(messageDto.getSenderUser()))
                .setRoom(new Room.Builder()
                        .setId(messageDto.getRoomId())
                        .build())
                .setText(messageDto.getText())
                .setType(MessageUtil.getEnum(messageDto.getType()))
                .build();
    }
}
