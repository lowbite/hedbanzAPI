package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.core.convert.converter.Converter;

public class MessageToMessageDTOConverter implements Converter<Message, MessageDTO> {
    @Override
    public MessageDTO convert(Message message) {
        UserToUserDTOConverter converter = new UserToUserDTOConverter();
        return new MessageDTO.MessageDTOBuilder()
                .setSenderUser(converter.convert(message.getSenderUser()))
                .setRoomId(message.getRoomId())
                .setText(message.getText())
                .setType(message.getType())
                .setCreateDate(message.getCreateDate())
                .setQuestionId(message.getQuestion() != null ? message.getQuestion().getId() : null)
                .createMessageDTO();
    }
}
