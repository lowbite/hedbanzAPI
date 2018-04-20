package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.core.convert.converter.Converter;

public class MessageToMessageDTOConverter implements Converter<Message, MessageDTO> {
    @Override
    public MessageDTO convert(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        UserToUserDTOConverter converter = new UserToUserDTOConverter();
        messageDTO.setSenderUser(converter.convert(message.getSenderUser()));
        messageDTO.setRoomId(message.getRoomId());
        messageDTO.setText(message.getText());
        messageDTO.setType(message.getType());
        messageDTO.setCreateDate(message.getCreateDate().getTime());
        return messageDTO;
    }
}
