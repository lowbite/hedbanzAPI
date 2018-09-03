package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.*;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.hedbanz.hedbanzAPI.constant.SocketEvents.*;

@Component
public class RoomEventHandler {
    private final Logger log = LoggerFactory.getLogger("RoomEventListener");


    private static final String USER_ID_FIELD = "userId";
    private static final String ROOM_ID_FIELD = "roomId";

    private static final double MIN_WIN_PERCENTAGE = 0.5;
    private static final double MIN_NEXT_GUESS_PERCENTAGE = 0.8;

    private final RoomService roomService;
    private final UserService userService;
    private final MessageService messageService;
    private final PlayerService playerService;
    private final FcmService fcmService;
    private final ConversionService conversionService;
    private final GameService gameService;

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventHandler(SocketIOServer server, RoomService roomService, UserService userService,
                            MessageService messageService, PlayerService playerService, FcmService fcmService,
                            @Qualifier("APIConversionService") ConversionService conversionService, GameService gameService) {
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(JOIN_ROOM_EVENT, UserToRoomDto.class, joinUserToRoom());
        this.socketIONamespace.addEventListener(LEAVE_ROOM_EVENT, UserToRoomDto.class, leaveUserFromRoom());
        this.socketIONamespace.addEventListener(CLIENT_TYPING_EVENT, UserToRoomDto.class, userStartTyping());
        this.socketIONamespace.addEventListener(CLIENT_STOP_TYPING_EVENT, UserToRoomDto.class, userStopTyping());
        this.socketIONamespace.addEventListener(CLIENT_MESSAGE_EVENT, MessageDto.class, sendUserMessage());
        this.socketIONamespace.addEventListener(CLIENT_SET_PLAYER_WORD_EVENT, Word.class, setPlayerWord());
        this.socketIONamespace.addEventListener(CLIENT_CONNECT_INFO_EVENT, ClientInfoDto.class, setClientInfo());
        this.socketIONamespace.addEventListener(CLIENT_RESTORE_ROOM_EVENT, ClientInfoDto.class, restoreRoom());
        this.socketIONamespace.addEventListener(CLIENT_USER_GUESSING_EVENT, QuestionDto.class, userGuessing());
        this.socketIONamespace.addEventListener(CLIENT_USER_ANSWERING_EVENT, QuestionDto.class, addVoteToQuestion());
        this.socketIONamespace.addEventListener(CLIENT_RESTART_GAME, UserToRoomDto.class, restartGame());
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
        this.conversionService = conversionService;
        this.playerService = playerService;
        this.fcmService = fcmService;
        this.gameService = gameService;
    }

    private DataListener<UserToRoomDto> restartGame() {
        return ((client, data, ackSender) -> {
            roomService.checkPlayerInRoom(data.getUserId(), data.getRoomId());
            if (gameService.isGameOver(data.getRoomId())) {
                log.info("Game restarting in room: " + data.getRoomId());
                messageService.deleteAllMessagesByRoom(data.getRoomId());
                Room room = gameService.restartGame(data.getRoomId());
                startGame(room);
            }
        });
    }

    private void startGame(Room room) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                sendPlayersSetWordsRequest(room.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkUser(ClientInfoDto clientInfo, SocketIOClient client, Room room) {
        if (room.getGameStatus() == GameStatus.SETTING_WORDS)
            for (Player player : room.getPlayers()) {
                if (clientInfo.getUserId().equals(player.getUser().getUserId())) {
                    sendWordSettingEvent(client, player, room);
                }
            }
    }

    private DataListener<ClientInfoDto> restoreRoom() {
        return (client, data, ackSender) -> {
            Room room = roomService.getRoom(data.getRoomId());
            checkUser(data, client, room);
            log.info("Set client info userId: " + data.getUserId() + " roomId: " + data.getRoomId());
            client.set(USER_ID_FIELD, data.getUserId());
            if (data.getRoomId() != null) {
                UserToRoomDto userToRoom = new UserToRoomDto.Builder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .build();
                roomService.checkPlayerInRoom(userToRoom.getUserId(), userToRoom.getRoomId());
                log.info("Setting player status active");
                Player player = playerService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                RoomDto roomDto = conversionService.convert(room, RoomDto.class);
                roomDto.setPlayers(room.getPlayers().stream()
                        .map(streamPlayer -> conversionService.convert(streamPlayer, PlayerDto.class))
                        .collect(Collectors.toList()));
                client.sendEvent(SERVER_RESTORE_ROOM_EVENT, roomDto);
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId()))
                        .sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));
                log.info("Client restore room set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    private DataListener<ClientInfoDto> setClientInfo() {
        return (client, data, ackSender) -> {
            Room room = roomService.getRoom(data.getRoomId());
            checkUser(data, client, room);
            log.info("Set client info userId: " + data.getUserId() + " roomId: " + data.getRoomId());
            client.set(USER_ID_FIELD, data.getUserId());
            if (data.getRoomId() != null) {
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                UserToRoomDto userToRoom = new UserToRoomDto.Builder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .build();
                log.info("Setting player status active");
                Player player = playerService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));
                List<Player> players = playerService.getPlayersFromRoom(data.getRoomId());
                List<PlayerDto> playerDtos = players.stream()
                        .map(streamPlayer -> conversionService.convert(streamPlayer, PlayerDto.class))
                        .collect(Collectors.toList());
                client.sendEvent(SERVER_PLAYERS_STATUS, playerDtos);
                log.info("Client reconnect set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    /**
     * This method removing user from room
     *
     * @return
     */
    private DataListener<UserToRoomDto> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            log.info("Adding left user message");
            messageService.addPlayerEventMessage(MessageType.LEFT_USER, data.getUserId(), data.getRoomId());
            log.info("User: " + data.getUserId() + " leaving from room: " + data.getRoomId());
            Room room = roomService.leaveUserFromRoom(data.getUserId(), data.getRoomId());
            User user = userService.getUser(data.getUserId());
            if (room.getGameStatus() == GameStatus.SETTING_WORDS) {
                log.info("Deleting setting word message");
                Player player = isOnlyOnePlayer(room);
                if (player != null && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                    Notification notification = new Notification("Last player in room!",
                            "You left the last player in room");
                    FcmPush.FcmPushData<UserToRoomDto> fcmPushData =
                            new FcmPush.FcmPushData<>(NotificationMessageType.LAST_PLAYER.getCode(),
                                    new UserToRoomDto.Builder()
                                            .setRoomId(room.getId())
                                            .build());
                    FcmPush fcmPush = new FcmPush.Builder().setNotification(notification)
                            .setTo(player.getUser().getFcmToken())
                            .setPriority("normal")
                            .setData(fcmPushData)
                            .build();
                    fcmService.sendPushNotification(fcmPush);
                }
                messageService.deleteSettingWordMessage(room.getId(), user.getUserId());
            }
            if (room.getCurrentPlayersNumber() == 0 || playersAbsent(room)) {
                log.info("Deleting room:" + room.getId());
                roomService.deleteRoom(room.getId());
            }
            client.leaveRoom(String.valueOf(data.getRoomId()));
            log.info("User: " + data.getUserId() + " - left from room: " + data.getRoomId());
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId()))
                    .sendEvent(LEFT_USER_EVENT, conversionService.convert(user, UserDto.class));
        };
    }

    private boolean playersAbsent(Room room) {
        for (Player player : room.getPlayers()) {
            if (player.getStatus() != PlayerStatus.LEFT) {
                return false;
            }
        }
        return true;
    }

    private Player isOnlyOnePlayer(Room room) {
        int activePLayers = 0;
        Player onlyOnePlayer = null;
        for (Player player : room.getPlayers()) {
            if (player.getStatus() != PlayerStatus.LEFT) {
                if (activePLayers == 1)
                    return null;
                activePLayers++;
                onlyOnePlayer = player;
            }
        }
        return onlyOnePlayer;
    }

    /**
     * This method joining user to room and checking is room full if true then start game
     *
     * @return
     */
    private DataListener<UserToRoomDto> joinUserToRoom() {
        return (client, data, ackSender) -> {
            client.set(USER_ID_FIELD, data.getUserId());
            client.set(ROOM_ID_FIELD, data.getRoomId());

            Room room = roomService.addUserToRoom(data.getUserId(), data.getRoomId(), data.getPassword());
            messageService.addPlayerEventMessage(MessageType.JOINED_USER, data.getUserId(), data.getRoomId());
            List<Friend> friends = userService.getUserFriends(data.getUserId());
            RoomDto resultRoom = conversionService.convert(room, RoomDto.class);
            resultRoom.setPlayers(room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class))
                    .collect(Collectors.toList()));
            for (Friend friend : friends) {
                for (PlayerDto player : resultRoom.getPlayers()) {
                    if (friend.getId().equals(player.getId())) {
                        if (friend.getIsAccepted())
                            player.setIsFriend(true);
                        else if (friend.getIsPending())
                            player.setIsPending(true);
                    }
                }
            }
            Player player = playerService.getPlayer(data.getUserId(), data.getRoomId());
            client.joinRoom(String.valueOf(room.getId()));
            client.sendEvent(ROOM_INFO_EVENT, resultRoom);
            socketIONamespace.getRoomOperations(String.valueOf(room.getId())).sendEvent(JOINED_USER_EVENT,
                    conversionService.convert(player, PlayerDto.class));

            int clientsNumber = socketIONamespace.getRoomOperations(String.valueOf(room.getId())).getClients().size();
            log.info("User " + data.getUserId() + " - joined to room: " + data.getRoomId());
            log.info("Players in the room " + room.getId() + " - " + room.getCurrentPlayersNumber());
            log.info("Clients in the room " + room.getId() + " - " + clientsNumber);

            //Start game
            if (room.getMaxPlayers().equals(room.getPlayers().size()))
                startGame(room);
        };
    }

    /**
     * Method send to all users in the room that some user started type
     *
     * @return
     */
    private DataListener<UserToRoomDto> userStartTyping() {
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_TYPING_EVENT);
        };
    }

    /**
     * Method send to all users in the room that some user stopped type
     *
     * @return
     */
    private DataListener<UserToRoomDto> userStopTyping() {
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_STOP_TYPING_EVENT);
            log.info("User stopped typing");
        };
    }

    /**
     * Method send out to all users in room message that user sent
     *
     * @return
     */
    private DataListener<MessageDto> sendUserMessage() {
        return (client, data, ackSender) -> {
            data.setType(MessageType.SIMPLE_MESSAGE.getCode());
            Message message = messageService.addMessage(conversionService.convert(data, Message.class));
            Room room = roomService.getRoom(data.getRoomId());
            for (Player player : room.getPlayers()) {
                if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                    MessageNotification messageNotification = conversionService.convert(message, MessageNotification.class);
                    Notification notification = new Notification("New message!",
                            "User " + messageNotification.getSenderName() + " sent a new message.");
                    FcmPush.FcmPushData<MessageNotification> fcmPushData =
                            new FcmPush.FcmPushData<>(NotificationMessageType.MESSAGE.getCode(), messageNotification);
                    FcmPush fcmPush = new FcmPush.Builder().setNotification(notification)
                            .setTo(player.getUser().getFcmToken())
                            .setPriority("normal")
                            .setData(fcmPushData)
                            .build();
                    fcmService.sendPushNotification(fcmPush);
                }
            }
            MessageDto resultMessage = conversionService.convert(message, MessageDto.class);
            resultMessage.setClientMessageId(data.getClientMessageId());
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, resultMessage);
            log.info("User send message: ", data);
        };
    }

    private DataListener<Word> setPlayerWord() {
        return (client, data, ackSender) -> {
            playerService.setPlayerWord(data);
            Message message = messageService.getSettingWordMessage(data.getRoomId(), data.getSenderId());
            SetWordDto setWordDto = conversionService.convert(message, SetWordDto.class);
            Player wordSetter = playerService.getPlayer(setWordDto.getSenderUser().getId(), setWordDto.getRoomId());
            Player wordReceiver = playerService.getPlayer(wordSetter.getWordSettingUserId(), setWordDto.getRoomId());
            setWordDto.setWordReceiverUser(conversionService.convert(wordReceiver.getUser(), UserDto.class));
            setWordDto.setWord(wordReceiver.getWord());
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, setWordDto);
            log.info("User set word: " + data.getWord());

            List<Player> players = playerService.getPlayersFromRoom(data.getRoomId());
            boolean gameIsReady = true;
            for (Player player : players) {
                if (TextUtils.isEmpty(player.getWord()))
                    gameIsReady = false;
            }
            if (gameIsReady) {
                Player player = gameService.startGuessing(data.getRoomId());
                if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                    FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GUESS_WORD.getCode(),
                            new SetWordNotification(player.getRoom().getId(), player.getRoom().getName()));
                    FcmPush fcmPush = new FcmPush.Builder()
                            .setTo(player.getUser().getFcmToken())
                            .setData(fcmPushData)
                            .setTo(player.getUser().getFcmToken())
                            .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                            .build();
                    fcmService.sendPushNotification(fcmPush);
                }
                Question newQuestion = messageService.addSettingQuestionMessage(data.getRoomId(), player.getUser().getUserId());
                PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                        .setPlayer(conversionService.convert(player, PlayerDto.class))
                        .setAttempt(player.getAttempt())
                        .setQuestionId(newQuestion.getId())
                        .build();
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_GUESSING_EVENT, playerGuessingDto);
                log.info("Players start guessing: " + player.getId());
            }
        };
    }

    /**
     * Inner method that sends events about user typing
     *
     * @param data
     * @param event
     */
    private void userTyping(UserToRoomDto data, String event) {
        Player player = playerService.getPlayer(data.getUserId(), data.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }
        HashMap<String, Long> userId = new HashMap<>();
        userId.put("userId", player.getUser().getUserId());
        socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(event, userId);
    }

    private void sendPlayersSetWordsRequest(long roomId) {
        Room room = gameService.setPlayersWordSetters(roomId);
        for (Player player : room.getPlayers())
            messageService.addEmptyWordSetMessage(room.getId(), player.getUser().getUserId());
        sendWords(socketIONamespace.getRoomOperations(String.valueOf(roomId)).getClients(), room.getPlayers());
        log.info("Game started in room: " + roomId);
    }

    private void sendWords(Collection<SocketIOClient> clients, List<Player> players) {
        for (SocketIOClient client : clients) {
            for (Player player : players) {
                if (player.getUser().getUserId().equals(client.get(USER_ID_FIELD))) {
                    Room room = roomService.getRoom(client.get(ROOM_ID_FIELD));
                    if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
                        FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.SET_WORD.getCode(),
                                new SetWordNotification(room.getId(), room.getName()));
                        FcmPush fcmPush = new FcmPush.Builder()
                                .setTo(player.getUser().getFcmToken())
                                .setData(fcmPushData)
                                .setPriority("normal")
                                .setNotification(new Notification("Set word time", "It's time to set word in room " + room.getName()))
                                .build();
                        fcmService.sendPushNotification(fcmPush);
                    }
                    if (client.isChannelOpen()) {
                        sendWordSettingEvent(client, player, room);
                    }
                    if (player.getStatus() == PlayerStatus.AFK) {
                        if (room.getGameStatus() == GameStatus.SETTING_WORDS) {
                            playerService.startAfkCountdown(client.get(USER_ID_FIELD), client.get(ROOM_ID_FIELD),
                                    socketIONamespace.getRoomOperations(String.valueOf(client.get(ROOM_ID_FIELD))));
                        }
                    }
                }
            }
        }
    }

    private void sendWordSettingEvent(SocketIOClient client, Player player, Room room) {
        Message message = messageService.getSettingWordMessage(room.getId(), player.getUser().getUserId());
        SetWordDto setWordDto = conversionService.convert(message, SetWordDto.class);
        Player wordSetter = playerService.getPlayer(setWordDto.getSenderUser().getId(), setWordDto.getRoomId());
        Player wordReceiver = playerService.getPlayer(wordSetter.getWordSettingUserId(), setWordDto.getRoomId());
        setWordDto.setWordReceiverUser(conversionService.convert(wordReceiver.getUser(), UserDto.class));
        client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, setWordDto);
    }


    private DataListener<QuestionDto> userGuessing() {
        return (client, data, ackSender) -> {
            Message message = messageService.addQuestionText(data.getQuestionId(), data.getText());
            QuestionDto resultMessage = conversionService.convert(message, QuestionDto.class);
            resultMessage.setClientMessageId(data.getClientMessageId());
            socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                    .sendEvent(SERVER_USER_ASKING_EVENT, resultMessage);
        };
    }

    private DataListener<QuestionDto> addVoteToQuestion() {
        return (client, data, ackSender) -> {
            Question question = messageService.addVote(Vote.VoteBuilder().setSenderId(data.getSenderUser().getId())
                    .setRoomId(data.getRoomId())
                    .setQuestionId(data.getQuestionId())
                    .setVoteType(data.getVote())
                    .build());

            socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                    .sendEvent(SERVER_USER_ANSWERING_EVENT, conversionService.convert(question, QuestionDto.class));
            Room room = roomService.getRoom(client.get(ROOM_ID_FIELD));
            Message message = messageService.getMessageByQuestionId(data.getQuestionId());
            Question lastQuestion = messageService.getLastQuestionInRoom(room.getId());
            if ((double) question.getWinVoters().size() / (room.getCurrentPlayersNumber() - 1) >= MIN_WIN_PERCENTAGE) {
                Player player = playerService.getPlayer(message.getSenderUser().getUserId(), room.getId());
                if (!player.getIsWinner()) {
                    player = playerService.setPlayerWinner(message.getSenderUser().getUserId(), room.getId());
                    socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                            .sendEvent(SERVER_USER_WIN_EVENT, conversionService.convert(player, PlayerDto.class));
                    log.info("Adding player win message");
                    messageService.addPlayerEventMessage(MessageType.USER_WIN, player.getUser().getUserId(), room.getId());

                    if (gameService.isGameOver(room.getId())) {
                        log.info("Setting room status to game over");
                        gameService.setGameOverStatus(room.getId());
                        for (Player roomPlayer : room.getPlayers()) {
                            if (roomPlayer.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(roomPlayer.getUser().getFcmToken())) {
                                FcmPush.FcmPushData<SetWordNotification> fcmPushData =
                                        new FcmPush.FcmPushData<>(NotificationMessageType.GAME_OVER.getCode(),
                                                new SetWordNotification(room.getId(), room.getName()));
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
                        socketIONamespace.getRoomOperations(String.valueOf(room.getId()))
                                .sendEvent(SERVER_GAME_OVER, new UserToRoomDto());
                        log.info("Adding game over message");
                        messageService.addRoomEventMessage(MessageType.GAME_OVER, room.getId());
                    } else
                        sendNextGuessingPlayerToRoom(room.getId());
                }
            } else if (lastQuestion.getId().equals(question.getId())) {
                double votersPercentage = (double) (question.getNoVoters().size() + question.getYesVoters().size())
                        / (room.getCurrentPlayersNumber() - 1);
                if (votersPercentage >= MIN_NEXT_GUESS_PERCENTAGE) {
                    sendNextGuessingPlayerToRoom(room.getId());
                }
            }
        };
    }

    private void sendNextGuessingPlayerToRoom(Long roomId) {
        log.info("Getting next guessing player");
        Player player = gameService.getNextGuessingPlayer(roomId);
        Room room = roomService.getRoom(roomId);
        if (player.getStatus() == PlayerStatus.AFK && !TextUtils.isEmpty(player.getUser().getFcmToken())) {
            log.info("Sending push notification to guessing player");
            FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GUESS_WORD.getCode(),
                    new SetWordNotification(room.getId(), room.getName()));
            FcmPush fcmPush = new FcmPush.Builder()
                    .setTo(player.getUser().getFcmToken())
                    .setData(fcmPushData)
                    .setTo(player.getUser().getFcmToken())
                    .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                    .build();
            fcmService.sendPushNotification(fcmPush);
            log.info("Starting afk countdown");
            playerService.startAfkCountdown(player.getUser().getUserId(), roomId, socketIONamespace.getRoomOperations(String.valueOf(roomId)));
        }
        log.info("Adding question message");
        Question newQuestion = messageService.addSettingQuestionMessage(roomId, player.getUser().getUserId());
        PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                .setPlayer(conversionService.convert(player, PlayerDto.class))
                .setAttempt(player.getAttempt())
                .setQuestionId(newQuestion.getId())
                .build();
        socketIONamespace.getRoomOperations(String.valueOf(roomId))
                .sendEvent(SERVER_USER_GUESSING_EVENT, playerGuessingDto);
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client disconnected userId: " + client.get(USER_ID_FIELD));
            Player player = playerService.getPlayer(client.get(USER_ID_FIELD), client.get(ROOM_ID_FIELD));
            if (player.getStatus() != PlayerStatus.LEFT) {
                log.info("Setting player: " + client.get(USER_ID_FIELD));
                player = playerService.setPlayerStatus(client.get(USER_ID_FIELD), client.get(ROOM_ID_FIELD), PlayerStatus.AFK);
                socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                        .sendEvent(SERVER_USER_AFK_EVENT, conversionService.convert(player, PlayerDto.class));
                log.info("Sent afk event!");
                Room room = roomService.getRoom(client.get(ROOM_ID_FIELD));
                if (room.getGameStatus() == GameStatus.SETTING_WORDS) {
                    BroadcastOperations roomOperations = socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)));
                    Long roomId = client.get(ROOM_ID_FIELD);
                    Long userId = client.get(USER_ID_FIELD);
                    playerService.startAfkCountdown(userId, roomId, roomOperations);
                }
            }
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Client connected! " + client.getHandshakeData().getAddress());
        };
    }
}
