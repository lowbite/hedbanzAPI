package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.transfer.MessageNotification;
import com.hedbanz.hedbanzAPI.utils.MessageTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
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

    private final ConversionService conversionService;
    private final FcmServiceImpl fcmService;
    private final CrudRoomRepository crudRoomRepository;
    private final CrudUserRepository crudUserRepository;
    private final CrudMessageRepository crudMessageRepository;
    private final CrudQuestionRepository crudQuestionRepository;
    private final CrudPlayerRepository crudPlayerRepository;

    @Autowired
    public MessageServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                              FcmServiceImpl fcmService, CrudRoomRepository crudRoomRepository, CrudUserRepository crudUserRepository,
                              CrudMessageRepository crudMessageRepository, CrudQuestionRepository crudQuestionRepository,
                              CrudPlayerRepository crudPlayerRepository) {
        this.conversionService = conversionService;
        this.fcmService = fcmService;
        this.crudRoomRepository = crudRoomRepository;
        this.crudUserRepository = crudUserRepository;
        this.crudMessageRepository = crudMessageRepository;
        this.crudQuestionRepository = crudQuestionRepository;
        this.crudPlayerRepository = crudPlayerRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MessageDto addMessage(MessageDto messageDto) {
        if (messageDto.getRoomId() == null || messageDto.getClientMessageId() == null || messageDto.getText() == null ||
                messageDto.getType() == null || messageDto.getSenderUser() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        User sender = crudUserRepository.findOne(messageDto.getSenderUser().getId());
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(messageDto.getSenderUser().getId(), messageDto.getRoomId());
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder().setSenderUser(sender)
                .setText(messageDto.getText())
                .setType(MessageType.SIMPLE_MESSAGE)
                .setCreateDate(new Timestamp(new Date().getTime()))
                .setQuestion(null)
                .setRoom(player.getRoom())
                .build();
        crudMessageRepository.saveAndFlush(message);

        for (Player roomPlayer : player.getRoom().getPlayers()) {
            if (roomPlayer.getStatus() == PlayerStatus.AFK && roomPlayer.getUser().getFcmToken() != null) {
                fcmService.sendPushNotification(buildFcmPushMessage(message, roomPlayer));
            }
        }

        MessageDto resultMessage = conversionService.convert(message, MessageDto.class);
        resultMessage.setClientMessageId(messageDto.getClientMessageId());
        return resultMessage;
    }

    private FcmPush buildFcmPushMessage(Message message, Player player) {
        MessageNotification messageNotification = conversionService.convert(message, MessageNotification.class);
        Notification notification = new Notification("New message!",
                "User " + messageNotification.getSenderName() + " sent a new message.");
        FcmPush.FcmPushData<MessageNotification> fcmPushData =
                new FcmPush.FcmPushData<>(NotificationMessageType.MESSAGE.getCode(), messageNotification);

        return new FcmPush.Builder().setNotification(notification)
                .setTo(player.getUser().getFcmToken())
                .setPriority("normal")
                .setData(fcmPushData)
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addEventMessage(MessageDto messageDto) {
        if (messageDto.getRoomId() == null || messageDto.getType() == null || messageDto.getSenderUser() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(messageDto.getSenderUser().getId(), messageDto.getRoomId());
        User sender = crudUserRepository.findOne(messageDto.getSenderUser().getId());
        if (!player.getRoom().getId().equals(messageDto.getRoomId()))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder().setSenderUser(sender)
                .setType(MessageTypeUtil.convertCodeIntoEnum(messageDto.getType()))
                .setQuestion(null)
                .setRoom(player.getRoom())
                .build();
        crudMessageRepository.saveAndFlush(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.NESTED)
    public Message addQuestionText(Long questionId, String text) {
        if (questionId == null || text == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Message message = crudMessageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        message.setText(text);
        message = crudMessageRepository.saveAndFlush(message);
        return (Message) message.clone();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Question addVote(Vote vote) {
        if (vote.getQuestionId() == null || vote.getSenderId() == null || vote.getRoomId() == null
                || vote.getVoteType() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(vote.getSenderId(), vote.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_PLAYER);
        }
        if (!player.getRoom().getId().equals(vote.getRoomId())) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }

        Question question = crudQuestionRepository.findOne(vote.getQuestionId());
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
        return crudQuestionRepository.saveAndFlush(question);
    }

    @Override
    public Question getLastQuestionInRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Pageable pageable = new PageRequest(0, 1);
        Page<Question> page = crudMessageRepository.findLastQuestionByRoomId(roomId, pageable);
        List<Question> questions = page.getContent();
        return questions.get(0);
    }

    @Transactional(readOnly = true)
    public Message getMessageByQuestionId(Long questionId) {
        Message message = crudMessageRepository.findMessageByQuestionId(questionId);
        if (message == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);
        return message;
    }

    @Transactional
    public Question addSettingQuestionMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        User user = crudUserRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);
        Room room = crudRoomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
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
        return crudMessageRepository.saveAndFlush(message).getQuestion();
    }

    @Transactional
    public Message addSettingWordMessage(Long roomId, Long senderId) {
        if (roomId == null || senderId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        User user = crudUserRepository.findOne(senderId);
        if (user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);

        Room room = crudRoomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(senderId, roomId);
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.Builder()
                .setSenderUser(user)
                .setRoom(room)
                .setType(MessageType.WORD_SETTING)
                .build();
        return crudMessageRepository.saveAndFlush(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessages(Long roomId, Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = crudMessageRepository.findAllMessages(pageable, roomId);
        ArrayList<Message> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
