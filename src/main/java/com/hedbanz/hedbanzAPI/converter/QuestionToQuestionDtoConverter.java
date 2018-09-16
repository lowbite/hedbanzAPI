package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class QuestionToQuestionDtoConverter implements Converter<Question, QuestionDto> {
    private final PlayerToPlayerDtoConverter playerToPlayerDtoConverter;

    public QuestionToQuestionDtoConverter(PlayerToPlayerDtoConverter playerToPlayerDtoConverter) {
        this.playerToPlayerDtoConverter = playerToPlayerDtoConverter;
    }

    @Override
    public QuestionDto convert(Question question) {
        return new QuestionDto.QuestionDTOBuilder().setQuestionId(question.getId())
                .setYesVoters(question.getYesVoters() != null ?
                        question.getYesVoters().stream().map(playerToPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setNoVoters(question.getNoVoters() != null ?
                        question.getNoVoters().stream().map(playerToPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setWinVoters(question.getWinVoters() != null ?
                        question.getWinVoters().stream().map(playerToPlayerDtoConverter::convert).collect(Collectors.toList()) : null)
                .setAttempt(question.getAttempt())
                .build();
    }
}
