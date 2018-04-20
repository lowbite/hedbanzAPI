package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.misc.IterableCollection;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.entity.error.UserError;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.utils.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class RoomEventListener {
    private final Logger log = LoggerFactory.getLogger("RoomEventListener");


    private static final String JOIN_ROOM_EVENT = "join-room";
    private static final String LEAVE_ROOM_EVENT = "leave-room";
    private static final String ROOM_INFO_EVENT = "joined-room";
    private static final String JOINED_USER_EVENT = "joined-user";
    private static final String LEFT_USER_EVENT = "left-user";
    private static final String CLIENT_TYPING_EVENT = "client-start-typing";
    private static final String CLIENT_STOP_TYPING_EVENT = "client-stop-typing";
    private static final String CLIENT_MESSAGE_EVENT = "client-msg";
    private static final String CLIENT_SET_PLAYER_WORD_EVENT = "client-set-word";
    private static final String SERVER_TYPING_EVENT = "server-start-typing";
    private static final String SERVER_STOP_TYPING_EVENT = "server-stop-typing";
    private static final String SERVER_MESSAGE_EVENT = "server-msg";
    private static final String SERVER_SET_PLAYER_WORD_EVENT = "server-set-word";
    private static final String SERVER_THOUGHT_PLAYER_WORD_EVENT = "server-thought-player-word";

    private static final String SERVER_ERROR = "server-error";

    private static final String USER_ID_FIELD = "userId";
    private static final String ROOM_ID_FIELD = "roomId";

    private final RoomService roomService;
    private final UserService userService;

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventListener(SocketIOServer server, RoomService roomService, UserService userService){
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(JOIN_ROOM_EVENT, UserToRoomDTO.class, joinUserToRoom());
        this.socketIONamespace.addEventListener(LEAVE_ROOM_EVENT, UserToRoomDTO.class, leaveUserFromRoom());
        this.socketIONamespace.addEventListener(CLIENT_TYPING_EVENT, UserToRoomDTO.class, userStartTyping());
        this.socketIONamespace.addEventListener(CLIENT_STOP_TYPING_EVENT, UserToRoomDTO.class, userStopTyping());
        this.socketIONamespace.addEventListener(CLIENT_MESSAGE_EVENT, MessageDTO.class, sendUserMessage());
        this.socketIONamespace.addEventListener(CLIENT_SET_PLAYER_WORD_EVENT, WordDTO.class, setPlayerWord());
        this.roomService = roomService;
        this.userService = userService;
    }

    /**
     * This method removing user from room
     * @return
     */
    private DataListener<UserToRoomDTO> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            UserDTO userDTO = roomService.leaveRoom(data);
            if(userDTO.getCustomError() != null){
                client.sendEvent(SERVER_ERROR, userDTO.getCustomError());
                return;
            }
            userDTO = userService.getUser(data.getUserId());
            if(userDTO.getCustomError() != null){
                client.sendEvent(SERVER_ERROR, userDTO.getCustomError());
                return;
            }
            client.leaveRoom(String.valueOf(data.getRoomId()));
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(LEFT_USER_EVENT, userDTO);
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
            if(roomDTO.getCustomError() != null){
                client.sendEvent(SERVER_ERROR, roomDTO.getCustomError());
                return;
            }
            UserDTO userDTO = userService.getUser(data.getUserId());
            if(userDTO.getCustomError() != null){
                client.sendEvent(SERVER_ERROR, userDTO.getCustomError());
                return;
            }
            client.joinRoom(String.valueOf(roomDTO.getId()));
            client.sendEvent(ROOM_INFO_EVENT, roomDTO);
            socketIONamespace.getRoomOperations(String.valueOf(roomDTO.getId())).sendEvent(JOINED_USER_EVENT, userDTO);

            log.info("User joined to room", data);

            //Start game
            if(roomDTO.getCurrentPlayersNumber() == roomDTO.getMaxPlayers()){
                sendPlayersSetWordsRequest(roomDTO.getId());
            }
        };
    }

    /**
     * Method send to all users in the room that some user started type
     * @return
     */
    private DataListener<UserToRoomDTO> userStartTyping(){
        return ((client, data, ackSender) -> {
            userTyping(data, SERVER_TYPING_EVENT);
        });
    }

    /**
     * Method send to all users in the room that some user stopped type
     * @return
     */
    private DataListener<UserToRoomDTO> userStopTyping(){
        return ((client, data, ackSender) -> {
            userTyping(data, SERVER_STOP_TYPING_EVENT);
            log.info("User stopped typing");
        });
    }

    /**
     * Method send out to all users in room message that user sent
     * @return
     */
    private DataListener<MessageDTO> sendUserMessage(){
        return ((client, data, ackSender) -> {
            MessageDTO message = roomService.addMessage(data);
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, message);
            log.info("User send message: ", data);
        });
    }

    private DataListener<WordDTO> setPlayerWord(){
        return ((client, data, ackSender) -> {
            WordDTO wordDTO = roomService.setPlayerWord(data);
            if(wordDTO.getError() != null){
                client.sendEvent(SERVER_ERROR, wordDTO.getError());
                return;
            }
            socketIONamespace.getRoomOperations(client.get(ROOM_ID_FIELD)).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, wordDTO);
            log.info("User set word: ", data);
        });
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
            client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT,  new WordDTO.WordDTOBuilder().setWordReceiverId(wordReceiverId)
                                                                            .createWordDTO());
            wordReceiverId = client.get(USER_ID_FIELD);
            log.info("Word setting send to: ", client);
        }
        iterator = clients.iterator();
        client = iterator.next();
        client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT,  new WordDTO.WordDTOBuilder().setWordReceiverId(wordReceiverId)
                                                                            .createWordDTO());
        log.info("Word setting send to: ",  client);
        log.info("Game started in room: " + roomId);
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            client.disconnect();
            UserToRoomDTO userToRoomDTO = new UserToRoomDTO();
            userToRoomDTO.setUserId(client.get(USER_ID_FIELD));
            userToRoomDTO.setRoomId(client.get(ROOM_ID_FIELD));
            UserDTO userDTO = roomService.leaveRoom(userToRoomDTO);
            if(userDTO.getCustomError() != null){
                client.sendEvent(SERVER_ERROR, userDTO.getCustomError());
                return;
            }
            log.info("Client disconnected!", client.getHandshakeData().getAddress());
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Client connected!", client.getHandshakeData().getAddress());
        };
    }
}
