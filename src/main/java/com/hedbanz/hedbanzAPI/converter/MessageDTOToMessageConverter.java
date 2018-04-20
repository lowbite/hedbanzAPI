package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.core.convert.converter.Converter;

public class MessageDTOToMessageConverter implements Converter<MessageDTO, Message> {
    @Override
    public Message convert(MessageDTO messageDTO) {
        Message message = new Message();
        UserDTOToUserConverter converter = new UserDTOToUserConverter();
        message.setSenderUser(converter.convert(messageDTO.getSenderUser()));
        message.setRoomId(messageDTO.getRoomId());
        message.setText(messageDTO.getText());
        message.setType(messageDTO.getType());
        return message;
    }
}
