package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.builder.*;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.utils.PlayersUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/game")
public class GameController {
    private static final double MIN_WIN_PERCENTAGE = 0.8;
    private static final double MIN_NEXT_GUESS_PERCENTAGE = 0.8;
    private final RoomService roomService;
    private final MessageService messageService;
    private final FcmService fcmService;
    private final PlayerService playerService;
    private final GameService gameService;
    private final ConversionService conversionService;

    private final Logger log = LoggerFactory.getLogger(GameController.class);

    @Autowired
    public GameController(RoomService roomService, MessageService messageService, FcmService fcmService,
                          PlayerService playerService, GameService gameService,
                          @Qualifier("APIConversionService") ConversionService conversionService) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.fcmService = fcmService;
        this.playerService = playerService;
        this.gameService = gameService;
        this.conversionService = conversionService;
    }

    @PostMapping(value = "/start/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<RoomDto> startGame(@PathVariable("roomId") long roomId) {
        Room room = gameService.setPlayersWordSetters(roomId);
        String wordReceiverName = null;
        for (Player player : room.getPlayers()) {
            messageService.addEmptyWordSetMessage(room.getId(), player.getUser().getUserId());

            if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                for (Player wordReceiver : room.getPlayers()) {
                    if (wordReceiver.getUser().getUserId().equals(player.getWordReceiverUserId())) {
                        wordReceiverName = wordReceiver.getUser().getLogin();
                        break;
                    }
                }
                PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                        .setSenderName(wordReceiverName)
                        .setRoomId(room.getId())
                        .setRoomName(room.getName())
                        .build();
                FcmPush fcmPush = new FcmPushDirector(new SetWordFcmPushBuilder())
                        .buildFcmPush(player.getUser().getFcmToken(), pushMessageDto);
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
            Room room = gameService.restartGame(roomId);
            resultRoomDto = conversionService.convert(room, RoomDto.class);
            resultRoomDto.setPlayers(
                    room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList())
            );
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultRoomDto);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/player/word", consumes = "application/json")
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
                PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                        .setRoomId(player.getRoom().getId())
                        .setRoomName(player.getRoom().getName())
                        .build();
                FcmPush fcmPush = new FcmPushDirector(new GuessWordFcmPushBuilder())
                        .buildFcmPush(player.getUser().getFcmToken(), pushMessageDto);
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

    @PutMapping(value = "/player/question")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<QuestionDto> addPlayerQuestion(@RequestBody QuestionDto questionDto) {
        Message message = messageService.addQuestionText(questionDto.getQuestionId(), questionDto.getText());
        List<Player> players = playerService.getPlayersFromRoom(message.getRoom().getId());
        List<String> afkUsersFcmTokens = new ArrayList<>();
        for (Player player: players) {
            if(player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())){
                afkUsersFcmTokens.add(player.getUser().getFcmToken());
            }
        }
        PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                .setSenderName(message.getSenderUser().getLogin())
                .setRoomName(message.getRoom().getName())
                .setRoomId(message.getRoom().getId())
                .setText(message.getText())
                .build();
        FcmPush fcmPush = new FcmPushDirector(new AskingQuestionFcmPushBuilder())
                .buildFcmPush(null, pushMessageDto);
        fcmService.sendPushNotificationsToUsers(fcmPush, afkUsersFcmTokens);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(message, QuestionDto.class));
    }

    @PutMapping(value = "/player/vote")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<QuestionDto> addPlayerVote(@RequestBody QuestionDto questionDto) {
        messageService.addVote(
                Vote.VoteBuilder()
                        .setSenderId(questionDto.getSenderUser().getId())
                        .setRoomId(questionDto.getRoomId())
                        .setQuestionId(questionDto.getQuestionId())
                        .setVoteType(questionDto.getVote())
                        .build()
        );
        Question resultQuestion = messageService.getQuestionByQuestionId(questionDto.getQuestionId());
        QuestionDto resultQuestionDto = conversionService.convert(resultQuestion, QuestionDto.class);
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
        if ((double) message.getQuestion().getWinVoters().size() / (PlayersUtil.getActivePlayersNumber(room.getPlayers()) - 1) >= MIN_WIN_PERCENTAGE) {
            player = playerService.getPlayer(message.getSenderUser().getUserId(), room.getId());
            if (!player.getIsWinner()) {
                player = playerService.setPlayerWinner(message.getSenderUser().getUserId(), room.getId());
                messageService.addPlayerEventMessage(MessageType.USER_WIN, player.getUser().getUserId(), room.getId());
                gameService.incrementUserGamesNumber(player.getRoom().getId(), player.getUser().getUserId());
            }
        } else if (lastQuestion.getId().equals(message.getQuestion().getId())) {
            double yesNoVotersPercentage = (double) (message.getQuestion().getNoVoters().size() + message.getQuestion().getYesVoters().size())
                    / (PlayersUtil.getActivePlayersNumber(room.getPlayers()) - 1);
            double allVotersPercentage = (double) (message.getQuestion().getNoVoters().size() + message.getQuestion().getYesVoters().size()
                    + message.getQuestion().getWinVoters().size()) / (PlayersUtil.getActivePlayersNumber(room.getPlayers()) - 1);
            if (yesNoVotersPercentage >= MIN_NEXT_GUESS_PERCENTAGE || allVotersPercentage == 1) {
                return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, receiveNextGuessingPlayer(room.getId(), questionDto.getAttempt()));
            }
        }
        PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                .setPlayer(conversionService.convert(player, PlayerDto.class))
                .build();
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, playerGuessingDto);
    }

    @PostMapping(value = "/room/{roomId}/player-win")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<?> setPlayerWin(@RequestBody QuestionDto questionDto, @PathVariable("roomId") long roomId) {
        Room room = roomService.getRoom(roomId);
        Message message = messageService.getMessageByQuestionId(questionDto.getQuestionId());
        if ((double) message.getQuestion().getWinVoters().size() / (PlayersUtil.getActivePlayersNumber(room.getPlayers()) - 1) >= MIN_WIN_PERCENTAGE) {
            Player player = playerService.getPlayer(message.getSenderUser().getUserId(), room.getId());
            if (!player.getIsWinner()) {
                player = playerService.setPlayerWinner(message.getSenderUser().getUserId(), room.getId());
                messageService.addPlayerEventMessage(MessageType.USER_WIN, player.getUser().getUserId(), room.getId());
                messageService.addEmptyWordSetMessage(roomId, player.getUser().getUserId());
                gameService.incrementUserGamesNumber(player.getRoom().getId(), player.getUser().getUserId());
                return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(player, PlayerDto.class));
            } else {
                throw ExceptionFactory.create(UserError.ALREADY_WIN);
            }
        } else {
            throw ExceptionFactory.create(UserError.NOT_ENOUGH_VOTES_TO_WIN);
        }
    }

    @PostMapping(value = "/room/{roomId}/is-game-over")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<GameOverDto> checkGameOver(@PathVariable("roomId") long roomId) {
        Room room = roomService.getRoom(roomId);
        if (gameService.isGameOver(roomId)) {
            gameService.setGameOverStatus(roomId);
            for (Player roomPlayer : room.getPlayers()) {
                if (roomPlayer.getStatus() == com.hedbanz.hedbanzAPI.constant.PlayerStatus.AFK &&
                        !TextUtils.isEmpty(roomPlayer.getUser().getFcmToken())) {
                    PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                            .setRoomId(room.getId())
                            .setRoomName(room.getName())
                            .build();
                    FcmPush fcmPush = new FcmPushDirector(new GameOverFcmPushBuilder())
                            .buildFcmPush(roomPlayer.getUser().getFcmToken(), pushMessageDto);
                    fcmService.sendPushNotification(fcmPush);
                }
            }
            messageService.addRoomEventMessage(MessageType.GAME_OVER, room.getId());
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, new GameOverDto(true));
        } else
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, new GameOverDto(false));
    }

    @RequestMapping(value = "/room/{roomId}/next-player", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerGuessingDto> getNextGuessingPlayer(@PathVariable("roomId") long roomId, @RequestBody QuestionDto questionDto) {
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, receiveNextGuessingPlayer(roomId, questionDto.getAttempt()));
    }

    private PlayerGuessingDto receiveNextGuessingPlayer(Long roomId, Integer currentAttempt) {
        try {
            log.info("Getting next player current attempt: " + currentAttempt);
            Player player = gameService.getNextGuessingPlayer(roomId, currentAttempt);
            Room room = roomService.getRoom(roomId);
            if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                        .setRoomId(room.getId())
                        .setRoomName(room.getName())
                        .build();
                FcmPush fcmPush = new FcmPushDirector(new GuessWordFcmPushBuilder())
                        .buildFcmPush(player.getUser().getFcmToken(), pushMessageDto);
                fcmService.sendPushNotification(fcmPush);
            }
            Question newQuestion = messageService.addSettingQuestionMessage(roomId, player.getUser().getUserId());
            log.info("New question" + newQuestion);
            return PlayerGuessingDto.PlayerGuessingDtoBuilder()
                    .setPlayer(conversionService.convert(player, PlayerDto.class))
                    .setAttempt(player.getAttempt())
                    .setQuestionId(newQuestion.getId())
                    .build();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw ExceptionFactory.create(RoomError.ALREADY_SENT_NEXT_PLAYER);
        }
    }
}
