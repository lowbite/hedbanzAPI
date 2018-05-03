package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.QuestionDTO;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CrudMessageRepository;
import com.hedbanz.hedbanzAPI.repository.CrudQuestionRepository;
import com.hedbanz.hedbanzAPI.repository.CrudRoomRepository;
import com.hedbanz.hedbanzAPI.repository.CrudUserRepository;
import com.hedbanz.hedbanzAPI.service.MessageService;
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

@Service
public class MessageServiceImpl implements MessageService {

    private final ConversionService conversionService;
    private final CrudRoomRepository crudRoomRepository;
    private final CrudUserRepository crudUserRepository;
    private final CrudMessageRepository crudMessageRepository;
    private final CrudQuestionRepository crudQuestionRepository;

    @Autowired
    public MessageServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                              CrudRoomRepository crudRoomRepository, CrudUserRepository crudUserRepository,
                              CrudMessageRepository crudMessageRepository, CrudQuestionRepository crudQuestionRepository) {
        this.conversionService = conversionService;
        this.crudRoomRepository = crudRoomRepository;
        this.crudUserRepository = crudUserRepository;
        this.crudMessageRepository = crudMessageRepository;
        this.crudQuestionRepository = crudQuestionRepository;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public MessageDTO addMessage(MessageDTO messageDTO) {
        if(messageDTO.getRoomId() == null || messageDTO.getClientMessageId() == null || messageDTO.getText() == null ||
                messageDTO.getType() == null || messageDTO.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Room room = crudRoomRepository.findOne(messageDTO.getRoomId());
        User sender = crudUserRepository.findOne(messageDTO.getSenderUser().getId());
        if(!room.isContainPlayer(conversionService.convert(sender, Player.class)))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = conversionService.convert(messageDTO, Message.class);
        message.setSenderUser(sender);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        message.setQuestion(null);
        message.setRoom(room);
        crudMessageRepository.saveAndFlush(message);

        MessageDTO resultMessage = conversionService.convert(message, MessageDTO.class);
        resultMessage.setClientMessageId(messageDTO.getClientMessageId());
        return resultMessage;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void addEventMessage(MessageDTO messageDTO) {
        if(messageDTO.getRoomId() == null || messageDTO.getType() == null || messageDTO.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Room room = crudRoomRepository.findOne(messageDTO.getRoomId());
        User sender = crudUserRepository.findOne(messageDTO.getSenderUser().getId());
        if(!room.isContainPlayer(conversionService.convert(sender, Player.class)))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = conversionService.convert(messageDTO, Message.class);
        message.setSenderUser(sender);
        message.setQuestion(null);
        message.setRoom(room);
        crudMessageRepository.saveAndFlush(message);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public MessageDTO addQuestionMessage(MessageDTO messageDTO){
        if(messageDTO.getRoomId() == null || messageDTO.getClientMessageId() == null || messageDTO.getText() == null ||
                messageDTO.getType() == null || messageDTO.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = crudRoomRepository.findOne(messageDTO.getRoomId());
        User sender = crudUserRepository.findOne(messageDTO.getSenderUser().getId());
        if(!room.isContainPlayer(conversionService.convert(sender, Player.class)))
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        Message message = conversionService.convert(messageDTO, Message.class);
        message.setSenderUser(sender);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        Question question = new Question.QuestionBuilder()
                                        .setNoNumber(0)
                                        .setYesNumber(0)
                                        .createQuestion();
        message.setQuestion(question);
        message.setRoom(room);
        message = crudMessageRepository.saveAndFlush(message);
        return conversionService.convert(message, MessageDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionDTO addVote(QuestionDTO questionDTO) {
        if(questionDTO.getQuestionId() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Question question = crudQuestionRepository.findOne(questionDTO.getQuestionId());
        if(questionDTO.getNoNumber() == 1){
            question.setNoNumber(question.getNoNumber() + 1);
            question = crudQuestionRepository.saveAndFlush(question);
        }else if(questionDTO.getYesNumber() == 1){
            question.setYesNumber(question.getYesNumber() + 1);
            question = crudQuestionRepository.saveAndFlush(question);
        }
        if(question == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_QUESTION);
        return conversionService.convert(question, QuestionDTO.class);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<MessageDTO> page = crudMessageRepository.findAllMessages(pageable, roomId);
        ArrayList<MessageDTO> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }
}
