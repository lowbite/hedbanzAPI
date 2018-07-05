package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.model.MessageNotification;
import org.springframework.core.convert.converter.Converter;

public class MessageToMessageNotificationDtoConverter implements Converter<Message, MessageNotification> {
    @Override
    public MessageNotification convert(Message message) {
        return MessageNotification.Builder()
                .setSenderName(message.getSenderUser().getLogin())
                .setText(message.getText())
                .setRoomName(message.getRoom().getName())
                .setRoomId(message.getRoom().getId())
                .build();
    }
}
