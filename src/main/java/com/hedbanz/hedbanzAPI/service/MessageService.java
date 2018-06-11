package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> getAllMessages(long roomId, int pageNumber);

    MessageDto addMessage(MessageDto messageDto);

    void addEventMessage(MessageDto messageDto);

    MessageDto addQuestionMessage(MessageDto messageDto);

    QuestionDto addVote(QuestionDto questionDto);
}
