package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.DTO.QuestionDTO;
import com.hedbanz.hedbanzAPI.entity.Question;
import org.springframework.core.convert.converter.Converter;

public class QuestionToQuestionDTOConversion implements Converter<Question, QuestionDTO> {
    @Override
    public QuestionDTO convert(Question question) {
        return new QuestionDTO.QuestionDTOBuilder()
                                .setId(question.getId())
                                .setNoNumber(question.getNoNumber())
                                .setYesNumber(question.getYesNumber())
                                .createQuestionDTO();
    }
}
