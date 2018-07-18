package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.*;
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
        if (inputMessage.getRoom().getId() == null || inputMessage.getText() == null ||
                inputMessage.getType() == null || inputMessage.getSenderUser() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        User sender = userRepository.findOne(inputMessage.getSenderUser().getUserId());
        Player player = playerRepository.findPlayerByUserIdAndRoomId(inputMessage.getSenderUser().getUserId(), inputMessage.getRoom().getId());
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

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
    public Message addPlayerEventMessage(MessageType type, Long userId, Long roomId) {
        if (userId == null || roomId == null || type == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
        Room room = roomRepository.findOne(roomId);
        User user = userRepository.findOne(userId);
        if (!player.getRoom().getId().equals(roomId))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        return messageRepository.saveAndFlush(Message.Builder()
                .setRoom(room)
                .setType(type)
                .setSenderUser(user)
                .setQuestion(null)
                .build());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Message addRoomEventMessage(MessageType type, Long roomId) {
        if (roomId == null || type == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room room = roomRepository.findOne(roomId);
        return messageRepository.saveAndFlush(Message.Builder()
                .setRoom(room)
                .setType(type)
                .setSenderUser(null)
                .setQuestion(null)
                .build());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.NESTED)
    public Message addQuestionText(Long questionId, String text) {
        if (questionId == null || text == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        message.setText(text);
        message = messageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Question addVote(Vote vote) {
        if (vote.getQuestionId() == null || vote.getSenderId() == null || vote.getRoomId() == null
                || vote.getVoteType() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = playerRepository.findPlayerByUserIdAndRoomId(vote.getSenderId(), vote.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_PLAYER);
        }
        if (!player.getRoom().getId().equals(vote.getRoomId())) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }

        Question question = questionRepository.findOne(vote.getQuestionId());
        if (question == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);

        if (vote.getVoteType() == NO.getCode()) {
            if (!question.addNoVoter(player))
                throw ExceptionFactory.create(RoomError.SUCH_PLAYER_ALREADY_VOTED);
            if (question.yesVotersContainPlayer(player))
                question.removeYesVoter(player);
            else if (question.winVotersContainPlayer(player))
                question.removeWinVoter(player);
        } else if (vote.getVoteType() == YES.getCode()) {
            if (!question.addYesVoter(player))
                throw ExceptionFactory.create(RoomError.SUCH_PLAYER_ALREADY_VOTED);
            if (question.noVotersContainPlayer(player))
                question.removeNoVoter(player);
            else if (question.winVotersContainPlayer(player))
                question.removeWinVoter(player);
        } else if (vote.getVoteType() == WIN.getCode()) {
            if (!question.addWinVoter(player))
                throw ExceptionFactory.create(RoomError.SUCH_PLAYER_ALREADY_VOTED);
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
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Pageable pageable = new PageRequest(0, 1);
        Page<Question> page = messageRepository.findLastQuestionByRoomId(roomId, pageable);
        List<Question> questions = page.getContent();
        return questions.get(0);
    }

    @Transactional(readOnly = true)
    public Message getMessageByQuestionId(Long questionId) {
        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);
        return message;
    }

    @Transactional
    public Question addSettingQuestionMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        User user = userRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);
        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        Player player = playerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
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
    public Message addSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        User user = userRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);

        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        Player player = playerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder()
                .setSenderUser(user)
                .setRoom(room)
                .setType(MessageType.WORD_SETTING)
                .build();
        return messageRepository.saveAndFlush(message);
    }

    @Transactional
    public void deleteSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_MESSAGE);
        messageRepository.delete(message);
    }

    @Transactional
    public Message getSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_MESSAGE);
        return (Message) message.clone();
    }

    @Transactional
    public void deleteAllMessagesByRoom(Long roomId) {
        if(roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        messageRepository.deleteAllByRoom_Id(roomId);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessages(Long roomId, Integer pageNumber) {
        if (roomRepository.findOne(roomId) == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = messageRepository.findAllMessages(pageable, roomId);
        ArrayList<Message> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
