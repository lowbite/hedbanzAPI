package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.model.Vote;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages(Long roomId, Integer pageNumber);

    Message addMessage(Message message);

    Message addEventMessage(Message message);

    Message addQuestionText(Long questionId, String text);

    Question addVote(Vote vote);

    Question getLastQuestionInRoom(Long roomId);

    Message getMessageByQuestionId(Long questionId);

    Question addSettingQuestionMessage(Long roomId, Long senderId);

    Message addSettingWordMessage(Long roomId, Long senderId);
}
