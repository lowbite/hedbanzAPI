package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.utils.MessageTypeUtil;
import org.springframework.core.convert.converter.Converter;

public class MessageDtoToMessageConverter implements Converter<MessageDto, Message> {
    @Override
    public Message convert(MessageDto messageDto) {
        UserDtoToUserConverter converter = new UserDtoToUserConverter();
        return Message.Builder()
                .setSenderUser(converter.convert(messageDto.getSenderUser()))
                .setRoom(new Room.Builder()
                        .setId(messageDto.getRoomId())
                        .build())
                .setText(messageDto.getText())
                .setType(MessageTypeUtil.convertCodeIntoEnum(messageDto.getType()))
                .build();
    }
}
