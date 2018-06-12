package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import com.hedbanz.hedbanzAPI.entity.Question;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class QuestionToQuestionDtoConverter implements Converter<Question, QuestionDto> {
    @Override
    public QuestionDto convert(Question question) {
        PlayerToPlayerDtoConverter converter = new PlayerToPlayerDtoConverter();
        return new QuestionDto.QuestionDTOBuilder()
                                .setId(question.getId())
                                .setNoVoters(
                                        question.getNoVoters().stream().map(converter::convert).collect(Collectors.toList()))
                                .setYesVoters(
                                        question.getYesVoters().stream().map(converter::convert).collect(Collectors.toList()))
                                .build();
    }
}
