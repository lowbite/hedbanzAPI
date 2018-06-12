package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.core.convert.converter.Converter;

public class MessageToMessageDtoConverter implements Converter<Message, MessageDto> {
    @Override
    public MessageDto convert(Message message) {
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        QuestionToQuestionDtoConverter toQuestionDtoConverter = new QuestionToQuestionDtoConverter();
        return new MessageDto.MessageDTOBuilder()
                .setSenderUser(toUserDtoConverter.convert(message.getSenderUser()))
                .setRoomId(message.getRoom().getId())
                .setText(message.getText())
                .setType(message.getType().getCode())
                .setCreateDate(message.getCreateDate())
                .setQuestion(message.getQuestion() != null ? toQuestionDtoConverter.convert(message.getQuestion()) : null)
                .createMessageDTO();
    }
}
