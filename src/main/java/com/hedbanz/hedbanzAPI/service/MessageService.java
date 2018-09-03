package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.model.Vote;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages(Long roomId, Integer pageNumber);

    Message addMessage(Message message);

    void addPlayerEventMessage(MessageType type, Long userId, Long roomId);

    void addRoomEventMessage(MessageType type, Long roomId);

    Message addQuestionText(Long questionId, String text);

    Question addVote(Vote vote);

    Question getLastQuestionInRoom(Long roomId);

    Message getMessageByQuestionId(Long questionId);

    Question addSettingQuestionMessage(Long roomId, Long senderId);

    void addEmptyWordSetMessage(Long roomId, Long senderId);

    void deleteSettingWordMessage(Long roomId, Long senderId);

    Message getSettingWordMessage(Long roomId, Long senderId);

    void deleteAllMessagesByRoom(Long roomId);
}
