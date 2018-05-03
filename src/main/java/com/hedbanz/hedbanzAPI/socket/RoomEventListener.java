package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.utils.ErrorUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RoomEventListener {
    private final Logger log = LoggerFactory.getLogger("RoomEventListener");
    public static CopyOnWriteArrayList<WordDTO> sendWordEventClientsList = new CopyOnWriteArrayList<>();

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

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventListener(SocketIOServer server, RoomService roomService, UserService userService, MessageService messageService){
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(JOIN_ROOM_EVENT, UserToRoomDTO.class, joinUserToRoom());
        this.socketIONamespace.addEventListener(LEAVE_ROOM_EVENT, UserToRoomDTO.class, leaveUserFromRoom());
        this.socketIONamespace.addEventListener(CLIENT_TYPING_EVENT, UserToRoomDTO.class, userStartTyping());
        this.socketIONamespace.addEventListener(CLIENT_STOP_TYPING_EVENT, UserToRoomDTO.class, userStopTyping());
        this.socketIONamespace.addEventListener(CLIENT_MESSAGE_EVENT, MessageDTO.class, sendUserMessage());
        this.socketIONamespace.addEventListener(CLIENT_SET_PLAYER_WORD_EVENT, WordDTO.class, setPlayerWord());
        this.socketIONamespace.addEventListener(CLIENT_CONNECT_INFO_EVENT, ClientInfoDTO.class, setClientInfo());
        this.socketIONamespace.addEventListener(CLIENT_RESTORE_ROOM_EVENT, ClientInfoDTO.class, restoreRoom());
        this.socketIONamespace.addEventListener(CLIENT_USER_GUESSING_EVENT, MessageDTO.class, userGuessing());
        this.socketIONamespace.addEventListener(CLIENT_USER_ANSWERING_EVENT, QuestionDTO.class, userAnswering());
        this.roomService = roomService;
        this.userService = userService;
        this.messageService = messageService;
    }

    private void checkUser(ClientInfoDTO clientInfo, SocketIOClient client){
        WordDTO word;
        Iterator<WordDTO> iterator = sendWordEventClientsList.iterator();
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

    private DataListener<ClientInfoDTO> restoreRoom() {
        return (client, data, ackSender) -> {
            checkUser(data, client);
            client.set(USER_ID_FIELD, data.getUserId());
            if(data.getRoomId() != null) {
                UserToRoomDTO userToRoom = new UserToRoomDTO.UserToRoomDTOBuilder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                roomService.checkPlayerInRoom(userToRoom);
                RoomDTO room = roomService.getRoom(data.getRoomId());
                client.sendEvent(SERVER_RESTORE_ROOM_EVENT, room);
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                PlayerDTO player = roomService.setPlayerAFK(userToRoom, false);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, player);

                log.info("Client restore room set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    private DataListener<ClientInfoDTO> setClientInfo() {
        return (client, data, ackSender) -> {
            log.info("setClientInfo: " + client.getHandshakeData().getAddress());
            checkUser(data, client);

            client.set(USER_ID_FIELD, data.getUserId());
            if(data.getRoomId() != null) {
                client.set(ROOM_ID_FIELD, data.getRoomId());
                client.joinRoom(String.valueOf(data.getRoomId()));
                UserToRoomDTO userToRoom = new UserToRoomDTO.UserToRoomDTOBuilder()
                        .setUserId(data.getUserId())
                        .setRoomId(data.getRoomId())
                        .createUserToRoomDTO();
                PlayerDTO player = roomService.setPlayerAFK(userToRoom, false);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_RETURNED_EVENT, player);
                log.info("setClientInfo: " + client.getHandshakeData().getAddress() + " sent afk event");
                UserDTO user = new UserDTO.UserDTOBuilder()
                        .setId(data.getUserId())
                        .createUserDTO();

                MessageDTO messageDTO = new MessageDTO.MessageDTOBuilder()
                        .setSenderUser(user)
                        .setRoomId(client.get(ROOM_ID_FIELD))
                        .setType(MessageType.USER_RETURNED.getCode())
                        .createMessageDTO();
                messageService.addEventMessage(messageDTO);

                log.info("Client reconnect set userId: " + data.getUserId() + ", roomId: " + data.getRoomId());
            }
        };
    }

    /**
     * This method removing user from room
     * @return
     */
    private DataListener<UserToRoomDTO> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            roomService.leaveRoom(data);
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
    private DataListener<UserToRoomDTO> joinUserToRoom() {
        return (client, data, ackSender) -> {
            client.set(USER_ID_FIELD, data.getUserId());
            client.set(ROOM_ID_FIELD, data.getRoomId());

            RoomDTO roomDTO = roomService.addUserToRoom(data);
            UserDTO userDTO = userService.getUser(data.getUserId());
            client.joinRoom(String.valueOf(roomDTO.getId()));
            client.sendEvent(ROOM_INFO_EVENT, roomDTO);
            socketIONamespace.getRoomOperations(String.valueOf(roomDTO.getId())).sendEvent(JOINED_USER_EVENT, userDTO);

            int clientsNumber = socketIONamespace.getRoomOperations(String.valueOf(roomDTO.getId())).getClients().size();
            log.info("User " + data.getUserId() + " - joined to room: " + data.getRoomId() );
            log.info("Players in the room " + roomDTO.getId() + " - " + roomDTO.getCurrentPlayersNumber());
            log.info("Clients in the room " + roomDTO.getId() + " - " + clientsNumber);

            //Start game
            new Thread(()-> {
                try {
                    Thread.sleep(1000);
                    if (roomDTO.getMaxPlayers().equals(roomDTO.getPlayers().size()) &&
                           roomDTO.getCurrentPlayersNumber().equals(clientsNumber)) {
                        if (roomService.startGame(roomDTO.getId()))
                            sendPlayersSetWordsRequest(roomDTO.getId());
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
    private DataListener<UserToRoomDTO> userStartTyping(){
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_TYPING_EVENT);
        };
    }

    /**
     * Method send to all users in the room that some user stopped type
     * @return
     */
    private DataListener<UserToRoomDTO> userStopTyping(){
        return (client, data, ackSender) -> {
            userTyping(data, SERVER_STOP_TYPING_EVENT);
            log.info("User stopped typing");
        };
    }

    /**
     * Method send out to all users in room message that user sent
     * @return
     */
    private DataListener<MessageDTO> sendUserMessage(){
        return (client, data, ackSender) -> {
            data.setType(MessageType.SIMPLE_MESSAGE.getCode());
            MessageDTO message = messageService.addMessage(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, message);
            log.info("User send message: ", data);
        };
    }

    private DataListener<WordDTO> setPlayerWord(){
        return (client, data, ackSender) -> {
            roomService.setPlayerWord(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, data);
            log.info("User set word: " + data.getWord());

            List<PlayerDTO> players = roomService.getPlayers(data.getRoomId());
            boolean gameIsReady = true;
            for(PlayerDTO player : players){
                if(TextUtils.isEmpty(player.getWord()))
                    gameIsReady = false;
            }
            if(gameIsReady){
                PlayerDTO player = roomService.startGuessing(data.getRoomId());
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_USER_GUESSING_EVENT, player);
                log.info("Players start guessing: " + player.getId());
            }
        };
    }

    /**
     * Inner method that sends events about user typing
     * @param data
     * @param event
     */
    private void userTyping(UserToRoomDTO data, String event){
        UserDTO userDTO = userService.getUser(data.getUserId());
        HashMap<String, Long> userId = new HashMap<>();
        userId.put("userId", userDTO.getId());
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
            client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, ackCallback, new WordDTO.WordDTOBuilder().setWordReceiverId(wordReceiverId)
                    .createWordDTO());
        }else{
            sendWordEventClientsList.add(new WordDTO.WordDTOBuilder()
                                                    .setSenderId(client.get(USER_ID_FIELD))
                                                    .setWordReceiverId(wordReceiverId)
                                                    .createWordDTO());
        }
    }

    private DataListener<MessageDTO> userGuessing(){
        return (client, data, ackSender) -> {
            MessageDTO  message = messageService.addQuestionMessage(data);
            socketIONamespace.getRoomOperations(client.get(ROOM_ID_FIELD)).sendEvent(SERVER_USER_ASKING_EVENT, message);
            PlayerDTO player = roomService.nextGuessing(data.getRoomId());
            socketIONamespace.getRoomOperations(client.get(ROOM_ID_FIELD)).sendEvent(SERVER_USER_GUESSING_EVENT, player);
        };
    }

    private DataListener<QuestionDTO> userAnswering(){
        return (client, data, ackSender) -> {
            QuestionDTO question = messageService.addVote(data);
            socketIONamespace.getRoomOperations(client.get(ROOM_ID_FIELD)).sendEvent(SERVER_USER_ANSWERING_EVENT, question);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client disconnected! " + client.getHandshakeData().getAddress());

            UserToRoomDTO userToRoom = new UserToRoomDTO.UserToRoomDTOBuilder()
                    .setUserId(client.get(USER_ID_FIELD))
                    .setRoomId(client.get(ROOM_ID_FIELD))
                    .createUserToRoomDTO();
            PlayerDTO player = roomService.setPlayerAFK(userToRoom, true);
            if(player != null) {
                UserDTO user = new UserDTO.UserDTOBuilder()
                        .setId(client.get(USER_ID_FIELD))
                        .createUserDTO();
                socketIONamespace.getRoomOperations(String.valueOf(userToRoom.getRoomId()))
                        .sendEvent(SERVER_USER_AFK_EVENT, player);
                log.info("Client disconnect: " + client.getHandshakeData().getAddress() + " sent afk event");

                MessageDTO messageDTO = new MessageDTO.MessageDTOBuilder()
                        .setSenderUser(user)
                        .setRoomId(client.get(ROOM_ID_FIELD))
                        .setType(MessageType.USER_AFK.getCode())
                        .createMessageDTO();
                messageService.addEventMessage(messageDTO);
            }
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Client connected! " + client.getHandshakeData().getAddress());
        };
    }
}
