package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.stream.Collectors;

public class MessageToQuestionDtoConverter implements Converter<Message, QuestionDto> {
    @Override
    public QuestionDto convert(Message message) {
        PlayerToPlayerDtoConverter toPlayerDtoConverter = new PlayerToPlayerDtoConverter();
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        return new QuestionDto.QuestionDTOBuilder()
                .setId(message.getId())
                .setSenderUser(toUserDtoConverter.convert(message.getSenderUser()))
                .setRoomId(message.getRoom().getId())
                .setQuestionId(message.getQuestion().getId())
                .setCreateDate(new Timestamp(message.getCreatedAt().getTime()))
                .setText(message.getText())
                .setType(message.getType())
                .setNoVoters(message.getQuestion().getNoVoters() != null ?
                        message.getQuestion().getNoVoters().stream().map(toPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setYesVoters(message.getQuestion().getYesVoters() != null ?
                        message.getQuestion().getYesVoters().stream().map(toPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setWinVoters(message.getQuestion().getWinVoters() != null ?
                        message.getQuestion().getWinVoters().stream().map(toPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setAttempt(message.getQuestion().getAttempt())
                .build();
    }
}
