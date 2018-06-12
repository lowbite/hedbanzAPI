package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.utils.ErrorUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class RoomEventListener {
    private final Logger log = LoggerFactory.getLogger("RoomEventListener");
    public static CopyOnWriteArrayList<WordDto> sendWordEventClientsList = new CopyOnWriteArrayList<>();

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

    private static final String SERVER_ERROR = "server-error";

    private static final String USER_ID_FIELD = "userId";
    private static final String ROOM_ID_FIELD = "roomId";
    private static final String WORD_RECEIVER_ID_FIELD = "wordReceiverId";

    private static final Integer ACK_TIMEOUT = 3;

    private final RoomService roomService;
    private final UserService userService;
    private final MessageService messageService;
    private final PlayerService playerService;
    private final ConversionService conversionService;

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventListener(SocketIOServer server, RoomService roomService, UserService userService,
                             MessageService messageService, PlayerService playerService, @Qualifier("APIConversionService") ConversionService conversionService){
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
        this.socketIONamespace.addEventListener(CLIENT_USER_GUESSING_EVENT, MessageDto.class, userGuessing());
        this.socketIONamespace.addEventListener(CLIENT_USER_ANSWERING_EVENT, QuestionDto.class, userAnswering());
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
        this.conversionService = conversionService;
        this.playerService = playerService;
    }

    private void checkUser(ClientInfoDto clientInfo, SocketIOClient client){
        WordDto word;
        Iterator<WordDto> iterator = sendWordEventClientsList.iterator();
        while (iterator.hasNext()){
            word = iterator.next();
            if(word.getSenderId().equals(clientInfo.getUserId())){
                long wordReceiverId = word.getWordReceiverId();
                sendWord(client, wordReceiverId);
                sendWordEventClientsList.remove(word);
                break;
            }
        }
    }

    private DataListener<ClientInfoDto> restoreRoom() {
        return (client, data, ackSender) -> {
            checkUser(data, client);
            client.set(USER_ID_FIELD, data.getUserId());
            if(data.getRoomId() != null) {
                UserToRoomDto userToRoom = new UserToRoomDto.UserToRoomDTOBuilder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                roomService.checkPlayerInRoom(userToRoom.getUserId(), userToRoom.getRoomId());
                Room room = roomService.getRoom(data.getRoomId());
                if(room == null){
                    throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
                }
                RoomDto roomDto = conversionService.convert(room, RoomDto.class);
                roomDto.setPlayers(room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList()));
                client.sendEvent(SERVER_RESTORE_ROOM_EVENT, roomDto);
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                Player player = roomService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));

                log.info("Client restore room set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    private DataListener<ClientInfoDto> setClientInfo() {
        return (client, data, ackSender) -> {
            log.info("setClientInfo: " + client.getHandshakeData().getAddress());
            checkUser(data, client);

            client.set(USER_ID_FIELD, data.getUserId());
            if(data.getRoomId() != null) {
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                UserToRoomDto userToRoom = new UserToRoomDto.UserToRoomDTOBuilder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                Player player = roomService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.ACTIVE);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, conversionService.convert(player, PlayerDto.class));
                log.info("setClientInfo: " + client.getHandshakeData().getAddress() + " sent afk event");
                /*UserDto user = new UserDto.UserDTOBuilder()
                        .setId(data.getUserId())
                        .build();

                MessageDto messageDto = new MessageDto.MessageDTOBuilder()
                        .setSenderUser(user)
                        .setRoomId(client.get(ROOM_ID_FIELD))
                        .setType(MessageType.USER_RETURNED.getCode())
                        .createMessageDTO();
                messageService.addEventMessage(messageDto);*/

                log.info("Client reconnect set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    /**
     * This method removing user from room
     * @return
     */
    private DataListener<UserToRoomDto> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            roomService.leaveFromRoom(data.getUserId(), data.getRoomId());
            client.leaveRoom(String.valueOf(data.getRoomId()));
            log.info("User: " + data.getUserId() +  " - left from room: " + data.getRoomId() );
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId()))
                                    .sendEvent(LEFT_USER_EVENT, userService.getUser(data.getUserId()));
        };
    }

    /**
     * This method joining user to room and checking is room full if true then start game
     * @return
     */
    private DataListener<UserToRoomDto> joinUserToRoom() {
        return (client, data, ackSender) -> {
            client.set(USER_ID_FIELD, data.getUserId());
            client.set(ROOM_ID_FIELD, data.getRoomId());

            Room room = roomService.addUserToRoom(data.getUserId(), data.getRoomId(), data.getPassword());
            List<FriendDto> friends = userService.getUserAcceptedFriends(data.getUserId());
            RoomDto resultRoom = conversionService.convert(room, RoomDto.class);
            resultRoom.setPlayers(room.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList()));
            for (FriendDto friend : friends){
                for(PlayerDto player : resultRoom.getPlayers()) {
                    if (friend.getId().equals(player.getId())) {
                        player.setIsFriend(true);
                    }
                }
            }
            Player player = playerService.getPlayerByUserIdAndRoomId(data.getUserId(), data.getRoomId());
            client.joinRoom(String.valueOf(room.getId()));
            client.sendEvent(ROOM_INFO_EVENT, resultRoom);
            socketIONamespace.getRoomOperations(String.valueOf(room.getId())).sendEvent(JOINED_USER_EVENT, conversionService.convert(player, PlayerDto.class));

            int clientsNumber = socketIONamespace.getRoomOperations(String.valueOf(room.getId())).getClients().size();
            log.info("User " + data.getUserId() + " - joined to room: " + data.getRoomId() );
            log.info("Players in the room " + room.getId() + " - " + room.getCurrentPlayersNumber());
            log.info("Clients in the room " + room.getId() + " - " + clientsNumber);

            //Start game
            new Thread(()-> {
                try {
                    Thread.sleep(1000);
                    if (room.getMaxPlayers().equals(room.getPlayers().size()) &&
                           room.getCurrentPlayersNumber().equals(clientsNumber)) {
                        if (roomService.startGame(room.getId()))
                            sendPlayersSetWordsRequest(room.getId());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        };
    }

    /**
     * Method send to all users in the room that some user started type
     * @return
     */
    private DataListener<UserToRoomDto> userStartTyping(){
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_TYPING_EVENT);
        };
    }

    /**
     * Method send to all users in the room that some user stopped type
     * @return
     */
    private DataListener<UserToRoomDto> userStopTyping(){
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_STOP_TYPING_EVENT);
            log.info("User stopped typing");
        };
    }

    /**
     * Method send out to all users in room message that user sent
     * @return
     */
    private DataListener<MessageDto> sendUserMessage(){
        return (client, data, ackSender) -> {
            data.setType(MessageType.SIMPLE_MESSAGE.getCode());
            MessageDto message = messageService.addMessage(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, message);
            log.info("User send message: ", data);
        };
    }

    private DataListener<WordDto> setPlayerWord(){
        return (client, data, ackSender) -> {
            roomService.setPlayerWord(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, data);
            log.info("User set word: " + data.getWord());

            List<Player> players = roomService.getPlayers(data.getRoomId());
            boolean gameIsReady = true;
            for(Player player : players){
                if(TextUtils.isEmpty(player.getWord()))
                    gameIsReady = false;
            }
            if(gameIsReady){
                Player player = roomService.startGuessing(data.getRoomId());
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_GUESSING_EVENT, conversionService.convert(player, PlayerDto.class));
                log.info("Players start guessing: " + player.getId());
            }
        };
    }

    /**
     * Inner method that sends events about user typing
     * @param data
     * @param event
     */
    private void userTyping(UserToRoomDto data, String event){
        Player player = playerService.getPlayerByUserIdAndRoomId(data.getUserId(), data.getRoomId());
        if(player == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
        HashMap<String, Long> userId = new HashMap<>();
        userId.put("userId", player.getUser().getId());
        socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(event,userId);
    }

    private void sendPlayersSetWordsRequest(long roomId){
        Collection<SocketIOClient> clients = socketIONamespace.getRoomOperations(String.valueOf(roomId)).getClients();
        Iterator<SocketIOClient> iterator  = clients.iterator();
        if(!iterator.hasNext()){
            socketIONamespace.getRoomOperations(String.valueOf(roomId)).sendEvent(SERVER_ERROR, ErrorUtil.getError(RoomError.CANT_START_GAME));
            return;
        }

        long wordReceiverId = iterator.next().get(USER_ID_FIELD);
        SocketIOClient client;
        while(iterator.hasNext()){
            client = iterator.next();
            sendWord(client, wordReceiverId);
            wordReceiverId = client.get(USER_ID_FIELD);
            log.info("Word setting send to: " + client.get(USER_ID_FIELD));
        }
        iterator = clients.iterator();
        client = iterator.next();
        sendWord(client, wordReceiverId);
        log.info("Word setting send to: " + client.get(USER_ID_FIELD));
        log.info("Game started in room: " + roomId);
    }

    private void sendWord(SocketIOClient client, long wordReceiverId){
        if(client.isChannelOpen()) {
            SetWordAckCallback ackCallback = new SetWordAckCallback(Object.class, ACK_TIMEOUT);
            ackCallback.setClient(client);
            ackCallback.setWordReceiverId(wordReceiverId);
            client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, ackCallback, new WordDto.WordDTOBuilder().setWordReceiverId(wordReceiverId)
                    .createWordDTO());
        }else{
            sendWordEventClientsList.add(new WordDto.WordDTOBuilder()
                                                    .setSenderId(client.get(USER_ID_FIELD))
                                                    .setWordReceiverId(wordReceiverId)
                                                    .createWordDTO());
        }
    }

    private DataListener<MessageDto> userGuessing(){
        return (client, data, ackSender) -> {
            Message message = messageService.addQuestionMessage(data);
            QuestionDto resultMessage = conversionService.convert(message, QuestionDto.class);
            resultMessage.setClientMessageId(data.getClientMessageId());
            socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                                                    .sendEvent(SERVER_USER_ASKING_EVENT, resultMessage);
        };
    }

    private DataListener<QuestionDto> userAnswering(){
        return (client, data, ackSender) -> {
            Question question = messageService.addVote(Vote.VoteBuilder().setSenderId(data.getSenderId())
                                                                        .setRoomId(data.getRoomId())
                                                                        .setQuestionId(data.getQuestionId())
                                                                        .setVoteType(data.getVote())
                                                                        .build());

            socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                    .sendEvent(SERVER_USER_ANSWERING_EVENT, conversionService.convert(question, QuestionDto.class));

            Room room = roomService.getRoom(client.get(ROOM_ID_FIELD));
            double votersPercentage = (question.getNoVoters().size() + question.getYesVoters().size()) / room.getCurrentPlayersNumber();
            if(votersPercentage >= 0.8) {
                Player player = roomService.nextGuessing(data.getRoomId());
                socketIONamespace.getRoomOperations(String.valueOf((Long) client.get(ROOM_ID_FIELD)))
                        .sendEvent(SERVER_USER_GUESSING_EVENT, conversionService.convert(player, PlayerDto.class));
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client disconnected! " + client.getHandshakeData().getAddress());

            UserToRoomDto userToRoom = new UserToRoomDto.UserToRoomDTOBuilder()
                    .setUserId(client.get(USER_ID_FIELD))
                    .setRoomId(client.get(ROOM_ID_FIELD))
                    .createUserToRoomDTO();
            Player player = roomService.setPlayerStatus(userToRoom.getUserId(), userToRoom.getRoomId(), PlayerStatus.AFK);
            if(player != null) {
                /*UserDto user = new UserDto.UserDTOBuilder()
                        .setId(client.get(USER_ID_FIELD))
                        .build();*/
                socketIONamespace.getRoomOperations(String.valueOf(userToRoom.getRoomId()))
                        .sendEvent(SERVER_USER_AFK_EVENT, conversionService.convert(player, PlayerDto.class));
                log.info("Client disconnect: " + client.getHandshakeData().getAddress() + " sent afk event");

                // Save user afk message
                /*MessageDto messageDto = new MessageDto.MessageDTOBuilder()
                        .setSenderUser(user)
                        .setRoomId(client.get(ROOM_ID_FIELD))
                        .setType(MessageType.USER_AFK.getCode())
                        .createMessageDTO();
                messageService.addEventMessage(messageDto);*/
            }
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Client connected! " + client.getHandshakeData().getAddress());
        };
    }
}
