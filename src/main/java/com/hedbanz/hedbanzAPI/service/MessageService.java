package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.entity.Vote;
import com.hedbanz.hedbanzAPI.service.Implementation.MessageServiceImpl;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages(Long roomId, Integer pageNumber);

    MessageDto addMessage(MessageDto messageDto);

    void addEventMessage(MessageDto messageDto);

    Message addQuestionText(Long questionId, String text);

    Question addVote(Vote vote);

    Question getLastQuestionInRoom(Long roomId);

    Message getMessageByQuestionId(Long questionId);

    Question addSettingQuestionMessage(Long roomId, Long senderId);
}
