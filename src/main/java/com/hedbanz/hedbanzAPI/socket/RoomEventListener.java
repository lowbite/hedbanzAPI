package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserToRoomDTO;
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
    private static final String SERVER_TYPING_EVENT = "server-start-typing";
    private static final String SERVER_STOP_TYPING_EVENT = "server-stop-typing";
    private static final String SERVER_MESSAGE_EVENT = "server-msg";
    private static final String SERVER_SET_PLAYER_WORDS_EVENT = "server-set-words";
    private static final String CLIENT_SET_PLAYER_WORDS_EVENT = "client-set-words";
    private static final String SERVER_ERROR = "server-error";
    private static final String USER_ID_FIELD = "userId";
    private static final String ROOM_ID_FIELD = "roomId";

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

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
    }

    public void setPlayerWords(long roomId){
        List<SocketIOClient> clients = (List) socketIONamespace.getRoomOperations(String.valueOf(roomId)).getClients();
        clients.get(clients.size() - 1).sendEvent(SERVER_SET_PLAYER_WORDS_EVENT, clients.get(0).get(USER_ID_FIELD));
        for (int i = 0; i < clients.size() - 1; i++) {
            clients.get(i).sendEvent(SERVER_SET_PLAYER_WORDS_EVENT, clients.get(i + 1).get(USER_ID_FIELD));
        }
    }

    private DataListener<UserToRoomDTO> leaveUserFromRoom() {
        return new DataListener<UserToRoomDTO>() {
            @Override
            public void onData(SocketIOClient client, UserToRoomDTO data, AckRequest ackSender) throws Exception {
                roomService.leaveRoom(data);
                UserDTO userDTO = userService.getUser(data.getUserId());
                client.leaveRoom(String.valueOf(data.getRoomId()));
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(LEFT_USER_EVENT, userDTO);
            }
        };
    }

    private DataListener<UserToRoomDTO> joinUserToRoom() {
        return new DataListener<UserToRoomDTO>() {
            @Override
            public void onData(SocketIOClient client, UserToRoomDTO data, AckRequest ackSender) throws Exception {
                client.set(USER_ID_FIELD, data.getUserId());
                client.set(ROOM_ID_FIELD, data.getRoomId());
                RoomDTO roomDTO = roomService.addUserToRoom(data);
                UserDTO userDTO = userService.getUser(data.getUserId());
                client.joinRoom(String.valueOf(roomDTO.getId()));
                client.sendEvent(ROOM_INFO_EVENT, roomDTO);
                socketIONamespace.getRoomOperations(String.valueOf(roomDTO.getId())).sendEvent(JOINED_USER_EVENT, userDTO);
            }
        };
    }

    private DataListener<UserToRoomDTO> userStartTyping(){
        return new DataListener<UserToRoomDTO>() {
            @Override
            public void onData(SocketIOClient client, UserToRoomDTO data, AckRequest ackSender) throws Exception {
                userTyping(client, data, SERVER_TYPING_EVENT);
            }
        };
    }

    private DataListener<UserToRoomDTO> userStopTyping(){
        return new DataListener<UserToRoomDTO>() {
            @Override
            public void onData(SocketIOClient client, UserToRoomDTO data, AckRequest ackSender) throws Exception {
                userTyping(client, data, SERVER_STOP_TYPING_EVENT);
            }
        };
    }

    private DataListener<MessageDTO> sendUserMessage(){
        return new DataListener<MessageDTO>() {
            @Override
            public void onData(SocketIOClient client, MessageDTO data, AckRequest ackSender) throws Exception {
                MessageDTO message = roomService.addMessage(data);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, message);
            }
        };
    }

    private void userTyping(SocketIOClient client, UserToRoomDTO data, String event){
        UserDTO userDTO = userService.getUser(data.getUserId());
        HashMap<String, Long> userId = new HashMap<>();
        userId.put("userId", userDTO.getId());
        socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(event,userId);
    }

    private DisconnectListener onDisconnected() {
        return new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                client.disconnect();
                UserToRoomDTO userToRoomDTO = new UserToRoomDTO();
                userToRoomDTO.setUserId((Long)client.get(USER_ID_FIELD));
                userToRoomDTO.setRoomId((Long)client.get(ROOM_ID_FIELD));
                roomService.leaveRoom(userToRoomDTO);
                System.out.println("Client disconnected!" + client.getHandshakeData().getAddress());
            }
        };
    }

    private ConnectListener onConnected() {
        return new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("Client connected!" + client.getHandshakeData().getAddress());
            }
        };
    }
}
