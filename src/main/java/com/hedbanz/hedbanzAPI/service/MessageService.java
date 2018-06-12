package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.entity.Vote;
import com.hedbanz.hedbanzAPI.service.Implementation.MessageServiceImpl;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> getAllMessages(long roomId, int pageNumber);

    MessageDto addMessage(MessageDto messageDto);

    void addEventMessage(MessageDto messageDto);

    Message addQuestionMessage(MessageDto messageDto);

    Question addVote(Vote vote);
}
