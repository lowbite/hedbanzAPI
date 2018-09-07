package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.model.MessageNotification;
import com.hedbanz.hedbanzAPI.transfer.PushMessageDto;
import org.springframework.core.convert.converter.Converter;

public class MessageToPushMessageDtoConverter implements Converter<Message, PushMessageDto> {
    @Override
    public PushMessageDto convert(Message message) {
        return new PushMessageDto.Builder()
                .setSenderName(message.getSenderUser().getLogin())
                .setText(message.getText())
                .setRoomName(message.getRoom().getName())
                .setRoomId(message.getRoom().getId())
                .build();
    }
}
