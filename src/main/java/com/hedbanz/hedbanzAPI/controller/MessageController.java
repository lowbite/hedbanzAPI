package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.builder.FcmPushDirector;
import com.hedbanz.hedbanzAPI.builder.NewMessageFcmPushBuilder;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import com.hedbanz.hedbanzAPI.transfer.PushMessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/rooms/{roomId}/messages")
public class MessageController {
    private final RoomService roomService;
    private final MessageService messageService;
    private final ConversionService conversionService;
    private final FcmService fcmService;
    private final PlayerService playerService;

    @Autowired
    public MessageController(RoomService roomService, MessageService messageService,
                             @Qualifier("APIConversionService") ConversionService conversionService,
                             FcmService fcmService, PlayerService playerService) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.conversionService = conversionService;
        this.fcmService = fcmService;
        this.playerService = playerService;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/add", consumes = "application/json")
    @ResponseStatus(OK)
    public ResponseBody<MessageDto> addUserMessage(@PathVariable("roomId") long roomId, @RequestBody MessageDto messageDto) {
        Message message = messageService.addMessage(conversionService.convert(messageDto, Message.class));
        Room room = roomService.getRoom(roomId);
        for (Player player : room.getPlayers()) {
            if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                PushMessageDto pushMessageDto = conversionService.convert(message, PushMessageDto.class);
                FcmPush fcmPush = new FcmPushDirector(new NewMessageFcmPushBuilder())
                        .buildFcmPush(player.getUser().getFcmToken(), pushMessageDto);
                fcmService.sendPushNotification(fcmPush);
            }
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(message, MessageDto.class));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/waiting-players")
    @ResponseStatus(OK)
    public ResponseBody<?> addWaitingForPlayersMessage(@PathVariable("roomId") long roomId) {
        messageService.addRoomEventMessage(MessageType.WAITING_FOR_PLAYERS, roomId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/last-question")
    @ResponseStatus(OK)
    public ResponseBody<QuestionDto> getLastQuestion(@PathVariable("roomId") long roomId) {
        Question lastQuestion = messageService.getLastQuestionInRoom(roomId);
        QuestionDto questionDto = conversionService.convert(lastQuestion, QuestionDto.class);
        questionDto.setRoomId(roomId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, questionDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/question/{questionId}/questioner")
    @ResponseStatus(OK)
    public ResponseBody<PlayerDto> getQuestioner(@PathVariable("roomId") long roomId, @PathVariable("questionId") long questionId) {
        Message message = messageService.getMessageByQuestionId(questionId);
        Player player = playerService.getPlayer(message.getSenderUser().getUserId(), message.getRoom().getId());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(player, PlayerDto.class));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{userId}/empty-question")
    @ResponseStatus(OK)
    public ResponseBody<PlayerDto> deletePlayerEmptyQuestion(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId){
        messageService.deleteEmptyQuestions(roomId, userId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
