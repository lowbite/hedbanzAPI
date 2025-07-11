package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.MessageError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.Vote;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hedbanz.hedbanzAPI.constant.VoteType.*;

@Service
public class MessageServiceImpl implements MessageService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final QuestionRepository questionRepository;
    private final PlayerRepository playerRepository;

    private final Logger log = LoggerFactory.getLogger("Message service");

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
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (inputMessage.getText() == null)
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_TEXT);
        if (inputMessage.getSenderUser() == null) {
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_SENDER);
        }
        if (inputMessage.getSenderUser().getUserId() == null) {
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_SENDER);
        }
        User sender = userRepository.findById(inputMessage.getSenderUser().getUserId()).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_USER));
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(inputMessage.getSenderUser().getUserId(), inputMessage.getRoom().getId());
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder().setSenderUser(sender)
                .setText(inputMessage.getText())
                .setType(MessageType.SIMPLE_MESSAGE)
                .setQuestion(null)
                .setRoom(player.getRoom())
                .build();
        message = messageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addPlayerEventMessage(MessageType type, Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (type == null)
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_TYPE);

        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }
        Room room = roomRepository.findById(roomId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        User user = userRepository.findById(userId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_USER));
        if (!player.getRoom().getId().equals(roomId))
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
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
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (type == null)
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_TYPE);
        Room room = roomRepository.findById(roomId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
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
            throw ExceptionFactory.create(InputError.EMPTY_QUESTION_ID);
        if (text == null) {
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_TEXT);
        }
        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_QUESTION);
        message.setText(text);
        message = messageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addVote(Vote vote) {
        if (vote.getQuestionId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_QUESTION_ID);
        if (vote.getSenderId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (vote.getRoomId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (vote.getVoteType() == null)
            throw ExceptionFactory.create(InputError.EMPTY_VOTE_TYPE);

        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(vote.getSenderId(), vote.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }
        if (!player.getRoom().getId().equals(vote.getRoomId())) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }

        Question question = questionRepository.findQuestionById(vote.getQuestionId());
        if (question == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_QUESTION);

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
        questionRepository.saveAndFlush(question);
    }

    @Transactional(readOnly = true)
    public Question getLastQuestionInRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> page = messageRepository.findLastQuestionByRoomId(roomId, pageable);
        List<Question> questions = page.getContent();
        return questions.get(0);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Message getMessageByQuestionId(Long questionId) {
        if (questionId == null)
            throw ExceptionFactory.create(InputError.EMPTY_QUESTION_ID);

        Message message = messageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_QUESTION);
        return message;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Question getQuestionByQuestionId(Long questionId) {
        if (questionId == null)
            throw ExceptionFactory.create(InputError.EMPTY_QUESTION_ID);
        return questionRepository.findById(questionId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_QUESTION));
    }

    @Transactional
    public Question addSettingQuestionMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        User user = userRepository.findById(senderId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_USER));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        Question question = new Question.Builder()
                .setAttempt(player.getAttempt())
                .build();
        Message message = Message.Builder()
                .setSenderUser(user)
                .setText(null)
                .setType(MessageType.USER_QUESTION)
                .setQuestion(question)
                .setRoom(room)
                .build();
        return messageRepository.saveAndFlush(message).getQuestion();
    }

    @Transactional
    public void addEmptyWordSetMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);

        User user = userRepository.findById(senderId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_USER));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM));
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);

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
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message != null)
            messageRepository.delete(message);
    }

    @Transactional(readOnly = true)
    public Message getSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (senderId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        Message message = messageRepository.findMessageByWordSettingType(senderId, roomId);
        if (message == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_MESSAGE);
        return (Message) message.clone();
    }

    @Transactional
    public void deleteAllMessagesByRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        messageRepository.deleteAllByRoom_Id(roomId);
    }

    @Transactional
    public void deleteEmptyQuestions(Long roomId, Long userId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        if (userId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        messageRepository.deleteMessageWithEmptyQuestion(userId, roomId);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessages(Long roomId, Integer pageNumber) {
        if (roomRepository.findById(roomId).isEmpty())
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);
        Pageable pageable = PageRequest.of(pageNumber, Constants.ROOM_PAGE_SIZE);
        Page<Message> page = messageRepository.findAllMessages(pageable, roomId);
        ArrayList<Message> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
