package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.QuestionDTO;

import java.util.List;

public interface MessageService {
    List<MessageDTO> getAllMessages(long roomId, int pageNumber);

    MessageDTO addMessage(MessageDTO messageDTO);

    void addEventMessage(MessageDTO messageDTO);

    MessageDTO addQuestionMessage(MessageDTO messageDTO);

    QuestionDTO addVote(QuestionDTO questionDTO);
}
