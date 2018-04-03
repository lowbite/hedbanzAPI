package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import org.springframework.core.convert.converter.Converter;

public class MessageDTOToMessageConverter implements Converter<MessageDTO, Message> {
    @Override
    public Message convert(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setRoomId(messageDTO.getRoomId());
        message.setText(messageDTO.getText());
        message.setType(messageDTO.getType());
        return message;
    }
}
