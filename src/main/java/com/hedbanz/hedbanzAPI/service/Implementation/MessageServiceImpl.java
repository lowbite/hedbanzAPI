package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.VoteType;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.utils.MessageTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.hedbanz.hedbanzAPI.constant.VoteType.NO;
import static com.hedbanz.hedbanzAPI.constant.VoteType.YES;

@Service
public class MessageServiceImpl implements MessageService {

    private final ConversionService conversionService;
    private final CrudRoomRepository crudRoomRepository;
    private final CrudUserRepository crudUserRepository;
    private final CrudMessageRepository crudMessageRepository;
    private final CrudQuestionRepository crudQuestionRepository;
    private final CrudPlayerRepository crudPlayerRepository;

    @Autowired
    public MessageServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                              CrudRoomRepository crudRoomRepository, CrudUserRepository crudUserRepository,
                              CrudMessageRepository crudMessageRepository, CrudQuestionRepository crudQuestionRepository,
                              CrudPlayerRepository crudPlayerRepository) {
        this.conversionService = conversionService;
        this.crudRoomRepository = crudRoomRepository;
        this.crudUserRepository = crudUserRepository;
        this.crudMessageRepository = crudMessageRepository;
        this.crudQuestionRepository = crudQuestionRepository;
        this.crudPlayerRepository = crudPlayerRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MessageDto addMessage(MessageDto messageDto) {
        if(messageDto.getRoomId() == null || messageDto.getClientMessageId() == null || messageDto.getText() == null ||
                messageDto.getType() == null || messageDto.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        User sender = crudUserRepository.findOne(messageDto.getSenderUser().getId());
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(messageDto.getSenderUser().getId(), messageDto.getRoomId());
        if(!player.getRoom().getId().equals(messageDto.getRoomId()))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.MessageBuilder().setSenderUser(sender)
                                                .setText(messageDto.getText())
                                                .setType(MessageType.SIMPLE_MESSAGE)
                                                .setCreateDate(new Timestamp(new Date().getTime()))
                                                .setQuestion(null)
                                                .setRoom(player.getRoom())
                                                .build();
        crudMessageRepository.saveAndFlush(message);

        MessageDto resultMessage = conversionService.convert(message, MessageDto.class);
        resultMessage.setClientMessageId(messageDto.getClientMessageId());
        return resultMessage;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addEventMessage(MessageDto messageDto) {
        if(messageDto.getRoomId() == null || messageDto.getType() == null || messageDto.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(messageDto.getSenderUser().getId(), messageDto.getRoomId());
        User sender = crudUserRepository.findOne(messageDto.getSenderUser().getId());
        if(!player.getRoom().getId().equals(messageDto.getRoomId()))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = Message.MessageBuilder().setSenderUser(sender)
                                                .setType(MessageTypeUtil.convertCodeIntoEnum(messageDto.getType()))
                                                .setQuestion(null)
                                                .setRoom(player.getRoom())
                                                .build();
        crudMessageRepository.saveAndFlush(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Message addQuestionMessage(MessageDto messageDto){
        if(messageDto.getRoomId() == null || messageDto.getClientMessageId() == null || messageDto.getText() == null ||
                messageDto.getType() == null || messageDto.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(messageDto.getSenderUser().getId(), messageDto.getRoomId());
        User sender = crudUserRepository.findOne(messageDto.getSenderUser().getId());
        if(!player.getRoom().getId().equals(messageDto.getRoomId()))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Question question = new Question.QuestionBuilder()
                .createQuestion();
        Message message =  Message.MessageBuilder().setSenderUser(sender)
                                                .setType(MessageType.USER_QUESTION)
                                                .setCreateDate(new Timestamp(new Date().getTime()))
                                                .setQuestion(question)
                                                .setRoom(player.getRoom())
                                                .setText(messageDto.getText())
                                                .build();
        return crudMessageRepository.saveAndFlush(message);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Question addVote(Vote vote) {
        if(vote.getQuestionId() == null || vote.getSenderId() == null || vote.getRoomId() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = crudPlayerRepository.findOne(vote.getSenderId());
        if(player == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_PLAYER);
        }
        if(!player.getRoom().getId().equals(vote.getRoomId())){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }

        Question question = crudQuestionRepository.findOne(vote.getQuestionId());
        if(question == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);

        if(vote.getVoteType() == NO.getCode()){
            if(question.addNoVoter(player))
                throw ExceptionFactory.create(RoomError.SUCH_PLAYER_ALREADY_VOTED);
            if(question.yesVotersContainPlayer(player))
                question.removeYesVoter(player);
        }else if(vote.getVoteType() == YES.getCode()){
            if(!question.addYesVoter(player))
                throw ExceptionFactory.create(RoomError.SUCH_PLAYER_ALREADY_VOTED);
            if(question.noVotersContainPlayer(player))
                question.removeNoVoter(player);

        }
        return crudQuestionRepository.saveAndFlush(question);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<MessageDto> page = crudMessageRepository.findAllMessages(pageable, roomId);
        ArrayList<MessageDto> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
