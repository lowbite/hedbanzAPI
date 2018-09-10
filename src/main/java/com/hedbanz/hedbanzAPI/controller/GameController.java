package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.*;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/game")
public class GameController {
    private static final double MIN_WIN_PERCENTAGE = 0.8;
    private static final double MIN_NEXT_GUESS_PERCENTAGE = 0.8;
    @Autowired
    private RoomService roomService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private FcmService fcmService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;
    @Autowired
    @Qualifier("APIConversionService")
    private ConversionService conversionService;

    @PostMapping(value = "/start/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<RoomDto> startGame(@PathVariable("roomId") long roomId) {
        Room room = gameService.setPlayersWordSetters(roomId);
        String wordReceiverName = null;
        for (Player player : room.getPlayers()) {
            messageService.addEmptyWordSetMessage(room.getId(), player.getUser().getUserId());

            if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                for (Player wordReceiver: room.getPlayers()) {
                    if(wordReceiver.getUser().getUserId().equals(player.getWordReceiverUserId())){
                        wordReceiverName = wordReceiver.getUser().getLogin();
                        break;
                    }
                }
                FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(
                        NotificationMessageType.SET_WORD.getCode(),
                        new PushMessageDto.Builder()
                                .setSenderName(wordReceiverName)
                                .setRoomId(room.getId())
                                .setRoomName(room.getName())
                                .build());
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(player.getUser().getFcmToken())
                        .setData(fcmPushData)
                        .setPriority("normal")
                        .setNotification(new Notification("Set word time", "It's time to set word in room " + room.getName()))
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
        }
        RoomDto resultRoomDto = conversionService.convert(room, RoomDto.class);
        resultRoomDto.setPlayers(
                room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList())
        );
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultRoomDto);
    }

    @PostMapping(value = "/restart/room/{roomId}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<RoomDto> restartGame(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) {
        roomService.checkPlayerInRoom(userId, roomId);
        RoomDto resultRoomDto = null;
        if (gameService.isGameOver(roomId)) {
            messageService.deleteAllMessagesByRoom(roomId);
            Room room  = gameService.restartGame(roomId);
            resultRoomDto = conversionService.convert(room, RoomDto.class);
            resultRoomDto.setPlayers(
                    room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList())
            );
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultRoomDto);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/player/set-word", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<SetWordDto> setWordToPlayer(@RequestBody Word word) {
        playerService.setPlayerWord(word);
        Message message = messageService.getSettingWordMessage(word.getRoomId(), word.getSenderId());
        SetWordDto setWordDto = conversionService.convert(message, SetWordDto.class);
        Player wordSetter = playerService.getPlayer(setWordDto.getSenderUser().getId(), setWordDto.getRoomId());
        Player wordReceiver = playerService.getPlayer(wordSetter.getWordReceiverUserId(), setWordDto.getRoomId());
        setWordDto.setWordReceiverUser(conversionService.convert(wordReceiver.getUser(), UserDto.class));
        setWordDto.setWord(wordReceiver.getWord());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, setWordDto);
    }

    @PostMapping(value = "/start-guessing/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerGuessingDto> startGuessing(@PathVariable("roomId") long roomId) {
        List<Player> players = playerService.getPlayersFromRoom(roomId);
        boolean gameIsReady = true;
        for (Player player : players) {
            if (TextUtils.isEmpty(player.getWord()) && player.getStatus() != PlayerStatus.LEFT)
                gameIsReady = false;
        }
        PlayerGuessingDto playerGuessingDto = null;
        if (gameIsReady) {
            Player player = gameService.startGuessing(roomId);
            if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GUESS_WORD.getCode(),
                        new PushMessageDto.Builder()
                                .setRoomId(player.getRoom().getId())
                                .setRoomName(player.getRoom().getName())
                                .build());
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(player.getUser().getFcmToken())
                        .setData(fcmPushData)
                        .setTo(player.getUser().getFcmToken())
                        .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
            Question newQuestion = messageService.addSettingQuestionMessage(roomId, player.getUser().getUserId());
            playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                    .setPlayer(conversionService.convert(player, PlayerDto.class))
                    .setAttempt(player.getAttempt())
                    .setQuestionId(newQuestion.getId())
                    .build();
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, playerGuessingDto);
    }

    @PostMapping(value = "/player/add-question")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<QuestionDto> addPlayerQuestion(@RequestBody QuestionDto questionDto) {
        Message message = messageService.addQuestionText(questionDto.getQuestionId(), questionDto.getText());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(message, QuestionDto.class));
    }

    @PostMapping(value = "/player/add-vote")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<QuestionDto> addPlayerVote(@RequestBody QuestionDto questionDto) {
        Question question = messageService.addVote(
                Vote.VoteBuilder()
                        .setSenderId(questionDto.getSenderUser().getId())
                        .setRoomId(questionDto.getRoomId())
                        .setQuestionId(questionDto.getQuestionId())
                        .setVoteType(questionDto.getVote())
                        .build()
        );
        QuestionDto resultQuestionDto = conversionService.convert(question, QuestionDto.class);
        resultQuestionDto.setRoomId(questionDto.getRoomId());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(resultQuestionDto, QuestionDto.class));
    }

    @PostMapping(value = "/room/{roomId}/check-questioner-win")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerGuessingDto> checkQuestioner(@RequestBody QuestionDto questionDto, @PathVariable("roomId") long roomId) {
        Room room = roomService.getRoom(roomId);
        Message message = messageService.getMessageByQuestionId(questionDto.getQuestionId());
        Question lastQuestion = messageService.getLastQuestionInRoom(room.getId());
        Player player = null;
        if ((double) questionDto.getWinVoters().size() / (playerService.getActivePlayersNumber(room.getPlayers()) - 1) >= MIN_WIN_PERCENTAGE) {
            player = playerService.getPlayer(message.getSenderUser().getUserId(), room.getId());
            if (!player.getIsWinner()) {
                player = playerService.setPlayerWinner(message.getSenderUser().getUserId(), room.getId());
                messageService.addPlayerEventMessage(MessageType.USER_WIN, player.getUser().getUserId(), room.getId());
                gameService.incrementPlayerGamesNumber(player.getRoom().getId(), player.getUser().getUserId());
            }
        } else if (lastQuestion.getId().equals(questionDto.getQuestionId())) {
            double yesNoVotersPercentage = (double) (questionDto.getNoVoters().size() + questionDto.getYesVoters().size())
                    / (playerService.getActivePlayersNumber(room.getPlayers()) - 1);
            double allVotersPercentage = (double) (questionDto.getNoVoters().size() + questionDto.getYesVoters().size()
                    + questionDto.getWinVoters().size()) / (playerService.getActivePlayersNumber(room.getPlayers()) - 1);
            if (yesNoVotersPercentage >= MIN_NEXT_GUESS_PERCENTAGE || allVotersPercentage == 1) {
                return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, receiveNextGuessingPlayer(room.getId()));
            }
        }
        PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                .setPlayer(conversionService.convert(player, PlayerDto.class))
                .build();
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, playerGuessingDto);
    }

    @PostMapping(value = "/room/{roomId}/is-game-over")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerGuessingDto> checkGameOver(@PathVariable("roomId") long roomId) {
        Room room = roomService.getRoom(roomId);
        if (gameService.isGameOver(roomId)) {
            gameService.setGameOverStatus(roomId);
            for (Player roomPlayer : room.getPlayers()) {
                if (roomPlayer.getStatus() == com.hedbanz.hedbanzAPI.constant.PlayerStatus.AFK && !TextUtils.isEmpty(roomPlayer.getUser().getFcmToken())) {
                    FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(
                            NotificationMessageType.GAME_OVER.getCode(),
                            new PushMessageDto.Builder()
                                    .setRoomId(room.getId())
                                    .setRoomName(room.getName())
                                    .build()
                    );
                    FcmPush fcmPush = new FcmPush.Builder()
                            .setTo(roomPlayer.getUser().getFcmToken())
                            .setData(fcmPushData)
                            .setTo(roomPlayer.getUser().getFcmToken())
                            .setNotification(new Notification("Game over",
                                    "Game is over in room " + room.getName()))
                            .build();
                    fcmService.sendPushNotification(fcmPush);
                }
            }
            messageService.addRoomEventMessage(MessageType.GAME_OVER, room.getId());
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
        } else
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, receiveNextGuessingPlayer(roomId));
    }

    @RequestMapping(value = "/room/{roomId}/next-player", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerGuessingDto> getNextGuessingPlayer(@PathVariable("roomId") long roomId){
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, receiveNextGuessingPlayer(roomId));
    }

    private PlayerGuessingDto receiveNextGuessingPlayer(Long roomId) {
        Player player = gameService.getNextGuessingPlayer(roomId);
        Room room = roomService.getRoom(roomId);
        if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
            FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(
                    NotificationMessageType.GUESS_WORD.getCode(),
                    new PushMessageDto.Builder()
                            .setRoomId(room.getId())
                            .setRoomName(room.getName())
                            .build());
            FcmPush fcmPush = new FcmPush.Builder()
                    .setTo(player.getUser().getFcmToken())
                    .setData(fcmPushData)
                    .setTo(player.getUser().getFcmToken())
                    .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                    .build();
            fcmService.sendPushNotification(fcmPush);
        }
        Question newQuestion = messageService.addSettingQuestionMessage(roomId, player.getUser().getUserId());
        return PlayerGuessingDto.PlayerGuessingDtoBuilder()
                .setPlayer(conversionService.convert(player, PlayerDto.class))
                .setAttempt(player.getAttempt())
                .setQuestionId(newQuestion.getId())
                .build();
    }
}