package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RoomEventListener {
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

    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public RoomEventListener(SocketIOServer server){
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(JOIN_ROOM_EVENT, UserToRoomDTO.class, joinUserToRoom());
        this.socketIONamespace.addEventListener(LEAVE_ROOM_EVENT, UserToRoomDTO.class, leaveUserFromRoom());
        this.socketIONamespace.addEventListener(CLIENT_TYPING_EVENT, UserToRoomDTO.class, userStartTyping());
        this.socketIONamespace.addEventListener(CLIENT_STOP_TYPING_EVENT, UserToRoomDTO.class, userStopTyping());
        this.socketIONamespace.addEventListener(CLIENT_MESSAGE_EVENT, MessageDTO.class, sendUserMessage());
        this.socketIONamespace.addEventListener(CLIENT_SET_PLAYER_WORD_EVENT, SetWordDTO.class, setPlayerWord());
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
        });
    }

    private DataListener<SetWordDTO> setPlayerWord(){
        return ((client, data, ackSender) -> {
            SetWordDTO setWordDTO = roomService.setPlayerWord(data);
            if(setWordDTO.getError() != null){
                client.sendEvent(SERVER_ERROR, setWordDTO.getError());
                return;
            }
            setWordDTO.setWord(null);
            socketIONamespace.getRoomOperations(client.get(ROOM_ID_FIELD)).sendEvent(SERVER_THOUGHT_PLAYER_WORD_EVENT, setWordDTO);
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
        List<SocketIOClient> clients = (List) socketIONamespace.getRoomOperations(String.valueOf(roomId)).getClients();
        clients.get(clients.size() - 1).sendEvent(SERVER_SET_PLAYER_WORD_EVENT, clients.get(0).get(USER_ID_FIELD));
        for (int i = 0; i < clients.size() - 1; i++) {
            clients.get(i).sendEvent(SERVER_SET_PLAYER_WORD_EVENT, clients.get(i + 1).get(USER_ID_FIELD));
        }
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
            System.out.println("Client disconnected!" + client.getHandshakeData().getAddress());
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            System.out.println("Client connected!" + client.getHandshakeData().getAddress());
        };
    }
}
