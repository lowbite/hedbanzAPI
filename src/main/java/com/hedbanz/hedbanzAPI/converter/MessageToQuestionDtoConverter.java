package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import com.hedbanz.hedbanzAPI.entity.Question;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class MessageToQuestionDtoConverter implements Converter<Message, QuestionDto> {
    @Override
    public QuestionDto convert(Message message) {
        PlayerToPlayerDtoConverter converter = new PlayerToPlayerDtoConverter();
        return new QuestionDto.QuestionDTOBuilder()
                                .setId(message.getId())
                                .setSenderId(message.getSenderUser().getId())
                                .setRoomId(message.getRoom().getId())
                                .setCreateDate(message.getCreateDate())
                                .setText(message.getText())
                                .setType(message.getType())
                                .setNoVoters(message.getQuestion().getNoVoters() != null ?
                                        message.getQuestion().getNoVoters().stream().map(converter::convert).collect(Collectors.toList()) : null)
                                .setYesVoters(message.getQuestion().getYesVoters() != null ?
                                        message.getQuestion().getYesVoters().stream().map(converter::convert).collect(Collectors.toList()) : null)
                                .build();
    }
}
