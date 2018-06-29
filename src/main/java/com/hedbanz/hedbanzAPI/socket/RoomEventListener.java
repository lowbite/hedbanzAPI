package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
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

@Component
public class RoomEventListener {
    private final Logger log = LoggerFactory.getLogger("RoomEventListener");
    private static final String JOIN_ROOM_EVENT = "join-room";
    private static final String LEAVE_ROOM_EVENT = "leave-room";
    private static final String ROOM_INFO_EVENT = "joined-room";
    private static final String JOINED_USER_EVENT = "joined-user";
    private static final String LEFT_USER_EVENT = "left-user";
    private static final String CLIENT_CONNECT_INFO_EVENT = "client-connect-info";
    private static final String CLIENT_TYPING_EVENT = "client-start-typing";
    private static final String CLIENT_STOP_TYPING_EVENT = "client-stop-typing";
    private static final String CLIENT_MESSAGE_EVENT = "client-msg";
    private static final String CLIENT_SET_PLAYER_WORD_EVENT = "client-set-word";
    private static final String CLIENT_RESTORE_ROOM_EVENT = "client-restore-room";
    private static final String CLIENT_USER_GUESSING_EVENT = "client-user-guessing";
    private static final String CLIENT_USER_ANSWERING_EVENT = "client-user-answering";
    private static final String CLIENT_RESTART_GAME = "client-restart-game";
    private static final String SERVER_TYPING_EVENT = "server-start-typing";
    private static final String SERVER_STOP_TYPING_EVENT = "server-stop-typing";
    private static final String SERVER_MESSAGE_EVENT = "server-msg";
    private static final String SERVER_SET_PLAYER_WORD_EVENT = "server-set-word";
    private static final String SERVER_THOUGHT_PLAYER_WORD_EVENT = "server-thought-player-word";
    private static final String SERVER_RESTORE_ROOM_EVENT = "server-restore-room";
    private static final String SERVER_USER_AFK_EVENT = "server-user-afk";
    private static final String SERVER_USER_RETURNED_EVENT = "server-user-returned";
    private static final String SERVER_USER_GUESSING_EVENT = "server-user-guessing";
    private static final String SERVER_USER_ASKING_EVENT = "server-user-asking";
    private static final String SERVER_USER_ANSWERING_EVENT = "server-user-answering";
    private static final String SERVER_USER_WIN_EVENT = "server-user-win";
    private static final String SERVER_GAME_OVER = "server-game-over";


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

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventListener(SocketIOServer server, RoomService roomService, UserService userService,
                             MessageService messageService, PlayerService playerService, FcmService fcmService,
                             @Qualifier("APIConversionService") ConversionService conversionService) {
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(JOIN_ROOM_EVENT, UserToRoomDto.class, joinUserToRoom());
        this.socketIONamespace.addEventListener(LEAVE_ROOM_EVENT, UserToRoomDto.class, leaveUserFromRoom());
        this.socketIONamespace.addEventListener(CLIENT_TYPING_EVENT, UserToRoomDto.class, userStartTyping());
        this.socketIONamespace.addEventListener(CLIENT_STOP_TYPING_EVENT, UserToRoomDto.class, userStopTyping());
        this.socketIONamespace.addEventListener(CLIENT_MESSAGE_EVENT, MessageDto.class, sendUserMessage());
        this.socketIONamespace.addEventListener(CLIENT_SET_PLAYER_WORD_EVENT, WordDto.class, setPlayerWord());
        this.socketIONamespace.addEventListener(CLIENT_CONNECT_INFO_EVENT, ClientInfoDto.class, setClientInfo());
        this.socketIONamespace.addEventListener(CLIENT_RESTORE_ROOM_EVENT, ClientInfoDto.class, restoreRoom());
        this.socketIONamespace.addEventListener(CLIENT_USER_GUESSING_EVENT, QuestionDto.class, userGuessing());
        this.socketIONamespace.addEventListener(CLIENT_USER_ANSWERING_EVENT, QuestionDto.class, userAnswering());
        this.socketIONamespace.addEventListener(CLIENT_RESTART_GAME, UserToRoomDto.class, getRestartGame());
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
        this.conversionService = conversionService;
        this.playerService = playerService;
        this.fcmService = fcmService;
    }

    private DataListener<UserToRoomDto> getRestartGame() {
        return ((client, data, ackSender) -> {
            roomService.checkPlayerInRoom(data.getUserId(), data.getRoomId());
            Room room = roomService.restartGame(data.getRoomId());
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    sendPlayersSetWordsRequest(room.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void checkUser(ClientInfoDto clientInfo, SocketIOClient client, Room room) {
        if (room.getGameStatus() == GameStatus.SETTING_WORDS)
            for (Player player : room.getPlayers()) {
                if (clientInfo.getUserId().equals(player.getUser().getId())) {
                    client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, player.getUser().getId());
                }
            }
    }

    private DataListener<ClientInfoDto> restoreRoom() {
        return (client, data, ackSender) -> {
            Room room = roomService.getRoom(data.getRoomId());
            checkUser(data, client, room);
            client.set(USER_ID_FIELD, data.getUserId());
            if (data.getRoomId() != null) {
                UserToRoomDto userToRoom = new UserToRoomDto.Builder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                roomService.checkPlayerInRoom(userToRoom.getUserId(), userToRoom.getRoomId());
                Player player = roomService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                RoomDto roomDto = conversionService.convert(room, RoomDto.class);
                roomDto.setPlayers(room.getPlayers().stream().map(roomPlayer -> conversionService.convert(roomPlayer, PlayerDto.class)).collect(Collectors.toList()));
                client.sendEvent(SERVER_RESTORE_ROOM_EVENT, roomDto);
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));

                log.info("Client restore room set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    private DataListener<ClientInfoDto> setClientInfo() {
        return (client, data, ackSender) -> {
            Room room = roomService.getRoom(data.getRoomId());
            checkUser(data, client, room);
            client.set(USER_ID_FIELD, data.getUserId());
            if (data.getRoomId() != null) {
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                UserToRoomDto userToRoom = new UserToRoomDto.Builder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                Player player = roomService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));

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
            roomService.leaveFromRoom(data.getUserId(), data.getRoomId());
            client.leaveRoom(String.valueOf(data.getRoomId()));
            log.info("User: " + data.getUserId() + " - left from room: " + data.getRoomId());
            User user = userService.getUser(data.getUserId());
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId()))
                    .sendEvent(LEFT_USER_EVENT, conversionService.convert(user, UserDto.class));
        };
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
            Player player = playerService.getPlayerByUserIdAndRoomId(data.getUserId(), data.getRoomId());
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
                if (roomService.startGame(room.getId()))
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            sendPlayersSetWordsRequest(room.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
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
            MessageDto message = messageService.addMessage(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, message);
            log.info("User send message: ", data);
        };
    }

    private DataListener<WordDto> setPlayerWord() {
        return (client, data, ackSender) -> {
            roomService.setPlayerWord(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, data);
            log.info("User set word: " + data.getWord());

            List<Player> players = playerService.getPlayers(data.getRoomId());
            boolean gameIsReady = true;
            for (Player player : players) {
                if (TextUtils.isEmpty(player.getWord()))
                    gameIsReady = false;
            }
            if (gameIsReady) {
                Player player = roomService.startGuessing(data.getRoomId());
                FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GUESS_WORD.getCode(),
                        new SetWordNotification(player.getRoom().getId(), player.getRoom().getName()));
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(player.getUser().getFcmToken())
                        .setData(fcmPushData)
                        .setTo(player.getUser().getFcmToken())
                        .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                        .build();
                fcmService.sendPushNotification(fcmPush);
                Question newQuestion = messageService.addSettingQuestionMessage(data.getRoomId(), player.getUser().getId());
                PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                        .setPlayer(conversionService.convert(player, PlayerDto.class))
                        .setAttempts(player.getAttempt())
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
        Player player = playerService.getPlayerByUserIdAndRoomId(data.getUserId(), data.getRoomId());
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
        HashMap<String, Long> userId = new HashMap<>();
        userId.put("userId", player.getUser().getId());
        socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(event, userId);
    }

    private void sendPlayersSetWordsRequest(long roomId) {
        Room room = roomService.setPlayersWordSetters(roomId);
        sendWords(socketIONamespace.getRoomOperations(String.valueOf(roomId)).getClients(), room.getPlayers());
        log.info("Game started in room: " + roomId);
    }

    private void sendWords(Collection<SocketIOClient> clients, List<Player> players) {
        for (SocketIOClient client : clients) {
            for (Player player : players) {
                if (player.getUser().getId().equals(client.get(USER_ID_FIELD))) {
                    Room room = roomService.getRoom(client.get(ROOM_ID_FIELD));
                    FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData(NotificationMessageType.SET_WORD.getCode(),
                            new SetWordNotification(room.getId(), room.getName()));
                    FcmPush fcmPush = new FcmPush.Builder()
                            .setTo(player.getUser().getFcmToken())
                            .setData(fcmPushData)
                            .setPriority("normal")
                            .setNotification(new Notification("Set word time", "It's time to set word in room " + room.getName()))
                            .build();
                    fcmService.sendPushNotification(fcmPush);
                    if (client.isChannelOpen()) {
                        client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, new WordDto.WordDTOBuilder()
                                .setWordReceiverId(player.getWordSettingUserId())
                                .createWordDTO());
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


    private DataListener<QuestionDto> userGuessing() {
        return (client, data, ackSender) -> {
            Message message = messageService.addQuestionText(data.getQuestionId(), data.getText());
            QuestionDto resultMessage = conversionService.convert(message, QuestionDto.class);
            resultMessage.setClientMessageId(data.getClientMessageId());
            socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                    .sendEvent(SERVER_USER_ASKING_EVENT, resultMessage);
        };
    }

    private DataListener<QuestionDto> userAnswering() {
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
            Question lastQuestion = messageService.getLastQuestionInRoom(data.getRoomId());
            if ((double) question.getWinVoters().size() / (room.getCurrentPlayersNumber() - 1) >= MIN_WIN_PERCENTAGE) {
                Player player = playerService.getPlayerByUserIdAndRoomId(message.getSenderUser().getId(), data.getRoomId());
                if (player.getIsWinner()) {
                    player = playerService.setPlayerWinner(message.getSenderUser().getId(), data.getRoomId());
                    socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                            .sendEvent(SERVER_USER_WIN_EVENT, conversionService.convert(player, PlayerDto.class));
                    if (roomService.isGameOver(data.getRoomId())) {
                        for (Player roomPlayer : room.getPlayers()) {
                            FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GAME_OVER.getCode(),
                                    new SetWordNotification(roomPlayer.getRoom().getId(), roomPlayer.getRoom().getName()));
                            FcmPush fcmPush = new FcmPush.Builder()
                                    .setTo(roomPlayer.getUser().getFcmToken())
                                    .setData(fcmPushData)
                                    .setTo(roomPlayer.getUser().getFcmToken())
                                    .setNotification(new Notification("Game over", "Game is over in room " + room.getName()))
                                    .build();
                            fcmService.sendPushNotification(fcmPush);
                        }
                        socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_GAME_OVER, new UserToRoomDto());
                    } else
                        sendNextGuessingPLayer(data.getRoomId(), client);
                }
            } else if (lastQuestion.getId().equals(question.getId())) {
                double votersPercentage = (double) (question.getNoVoters().size() + question.getYesVoters().size())
                        / (room.getCurrentPlayersNumber() - 1);
                if (votersPercentage >= MIN_NEXT_GUESS_PERCENTAGE) {
                    sendNextGuessingPLayer(data.getRoomId(), client);
                }
            }
        };
    }

    private void sendNextGuessingPLayer(Long roomId, SocketIOClient client) {
        Player player = roomService.nextGuessing(roomId);
        FcmPush.FcmPushData<SetWordNotification> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.GUESS_WORD.getCode(),
                new SetWordNotification(player.getRoom().getId(), player.getRoom().getName()));
        FcmPush fcmPush = new FcmPush.Builder()
                .setTo(player.getUser().getFcmToken())
                .setData(fcmPushData)
                .setTo(player.getUser().getFcmToken())
                .setNotification(new Notification("Time to guess your word", "It's your turn to guess your word"))
                .build();
        fcmService.sendPushNotification(fcmPush);
        Question newQuestion = messageService.addSettingQuestionMessage(roomId, player.getUser().getId());
        PlayerGuessingDto playerGuessingDto = PlayerGuessingDto.PlayerGuessingDtoBuilder()
                .setPlayer(conversionService.convert(player, PlayerDto.class))
                .setAttempts(player.getAttempt())
                .setQuestionId(newQuestion.getId())
                .build();
        socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                .sendEvent(SERVER_USER_GUESSING_EVENT, playerGuessingDto);
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client disconnected userId: " + client.get(USER_ID_FIELD));
            Player player = playerService.getPlayerByUserIdAndRoomId(client.get(USER_ID_FIELD), client.get(ROOM_ID_FIELD));
            if (player != null && player.getStatus() != PlayerStatus.LEFT) {
                player = roomService.setPlayerStatus(client.get(USER_ID_FIELD), client.get(ROOM_ID_FIELD), PlayerStatus.AFK);
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
