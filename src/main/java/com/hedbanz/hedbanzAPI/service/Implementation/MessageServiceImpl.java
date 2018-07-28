package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.MessageError;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.Vote;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.*;

import static com.hedbanz.hedbanzAPI.constant.VoteType.NO;
import static com.hedbanz.hedbanzAPI.constant.VoteType.WIN;
import static com.hedbanz.hedbanzAPI.constant.VoteType.YES;

@Service
public class MessageServiceImpl implements MessageService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final QuestionRepository questionRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public MessageServiceImpl(RoomRepository roomRepository, UserRepository userRepository,
                              MessageRepository messageRepository, QuestionRepository questionRepository,
                              PlayerRepository playerRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.questionRepository = questionRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Message addMessage(Message inputMessage) {
        if (inputMessage.getRoom().getId() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (inputMessage.getText() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_TEXT);
        if (inputMessage.getType() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_TYPE);
        if (inputMessage.getSenderUser() == null) {
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_SENDER);
        }
        User sender = userRepository.findOne(inputMessage.getSenderUser().getUserId());
        Player player = playerRepository.findPlayerByUserIdAndRoomId(inputMessage.getSenderUser().getUserId(), inputMessage.getRoom().getId());
        if (player == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder().setSenderUser(sender)
                .setText(inputMessage.getText())
                .setType(MessageType.SIMPLE_MESSAGE)
                .setCreateDate(new Timestamp(new Date().getTime()))
                .setQuestion(null)
                .setRoom(player.getRoom())
                .build();
        message = messageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addPlayerEventMessage(MessageType type, Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (type == null)
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_TYPE);

        Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);
        }
        Room room = roomRepository.findOne(roomId);
        User user = userRepository.findOne(userId);
        if (!player.getRoom().getId().equals(roomId))
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);
        messageRepository.saveAndFlush(Message.Builder()
                .setRoom(room)
                .setType(type)
                .setSenderUser(user)
                .setQuestion(null)
                .build());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addRoomEventMessage(MessageType type, Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (type == null)
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_TYPE);
        Room room = roomRepository.findOne(roomId);
        messageRepository.saveAndFlush(Message.Builder()
                .setRoom(room)
                .setType(type)
                .setSenderUser(null)
                .setQuestion(null)
                .build());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.NESTED)
    public Message addQuestionText(Long questionId, String text) {
        if (questionId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_QUESTION_ID);
        if (text == null) {
            throw ExceptionFactory.create(MessageError.EMPTY_MESSAGE_TEXT);
        }
        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_QUESTION);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        message.setText(text);
        message = messageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Question addVote(Vote vote) {
        if (vote.getQuestionId() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_QUESTION_ID);
        if (vote.getSenderId() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);
        if (vote.getRoomId() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (vote.getVoteType() == null)
            throw ExceptionFactory.create(MessageError.EMPTY_VOTE_TYPE);

        Player player = playerRepository.findPlayerByUserIdAndRoomId(vote.getSenderId(), vote.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);
        }
        if (!player.getRoom().getId().equals(vote.getRoomId())) {
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);
        }

        Question question = questionRepository.findOne(vote.getQuestionId());
        if (question == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_QUESTION);

        if (vote.getVoteType() == NO.getCode()) {
            if (!question.addNoVoter(player))
                throw ExceptionFactory.create(MessageError.SUCH_PLAYER_ALREADY_VOTED);
            if (question.yesVotersContainPlayer(player))
                question.removeYesVoter(player);
            else if (question.winVotersContainPlayer(player))
                question.removeWinVoter(player);
        } else if (vote.getVoteType() == YES.getCode()) {
            if (!question.addYesVoter(player))
                throw ExceptionFactory.create(MessageError.SUCH_PLAYER_ALREADY_VOTED);
            if (question.noVotersContainPlayer(player))
                question.removeNoVoter(player);
            else if (question.winVotersContainPlayer(player))
                question.removeWinVoter(player);
        } else if (vote.getVoteType() == WIN.getCode()) {
            if (!question.addWinVoter(player))
                throw ExceptionFactory.create(MessageError.SUCH_PLAYER_ALREADY_VOTED);
            if (question.noVotersContainPlayer(player))
                question.removeNoVoter(player);
            else if (question.yesVotersContainPlayer(player))
                question.removeYesVoter(player);
        }
        return questionRepository.saveAndFlush(question);
    }

    @Override
    public Question getLastQuestionInRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);

        Pageable pageable = new PageRequest(0, 1);
        Page<Question> page = messageRepository.findLastQuestionByRoomId(roomId, pageable);
        List<Question> questions = page.getContent();
        return questions.get(0);
    }

    @Transactional(readOnly = true)
    public Message getMessageByQuestionId(Long questionId) {
        if (questionId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_QUESTION_ID);

        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_QUESTION);
        return message;
    }

    @Transactional
    public Question addSettingQuestionMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);
        User user = userRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER);
        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_ROOM);
        Player player = playerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);
        Question question = new Question.Builder()
                .setAttempt(player.getAttempt())
                .build();
        Message message = Message.Builder()
                .setSenderUser(user)
                .setText(null)
                .setType(MessageType.USER_QUESTION)
                .setCreateDate(null)
                .setQuestion(question)
                .setRoom(room)
                .build();
        return messageRepository.saveAndFlush(message).getQuestion();
    }

    @Transactional
    public void addSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);

        User user = userRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER);

        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_ROOM);

        Player player = playerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder()
                .setSenderUser(user)
                .setRoom(room)
                .setType(MessageType.WORD_SETTING)
                .build();
        messageRepository.saveAndFlush(message);
    }

    @Transactional
    public void deleteSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_MESSAGE);
        messageRepository.delete(message);
    }

    @Transactional
    public Message getSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_USER_ID);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_MESSAGE);
        return (Message) message.clone();
    }

    @Transactional
    public void deleteAllMessagesByRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(MessageError.EMPTY_ROOM_ID);
        messageRepository.deleteAllByRoom_Id(roomId);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessages(Long roomId, Integer pageNumber) {
        if (roomRepository.findOne(roomId) == null)
            throw ExceptionFactory.create(MessageError.NO_SUCH_ROOM);
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = messageRepository.findAllMessages(pageable, roomId);
        ArrayList<Message> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
