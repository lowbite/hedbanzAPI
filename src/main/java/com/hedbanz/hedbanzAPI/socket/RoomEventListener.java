package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.exception.UserException;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
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

    private DataListener<UserToRoomDTO> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            try {
                roomService.leaveRoom(data);
                UserDTO userDTO = userService.getUser(data.getUserId());
                client.leaveRoom(String.valueOf(data.getRoomId()));
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(LEFT_USER_EVENT, userDTO);
            }catch (RoomException roomException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, roomException.getError(), null));
            }catch (UserException userException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, userException.getError(), null));
            }
        };
    }

    private DataListener<UserToRoomDTO> joinUserToRoom() {
        return (client, data, ackSender) -> {
            try {
                RoomDTO roomDTO = roomService.addUserToRoom(data);
                UserDTO userDTO = userService.getUser(data.getUserId());
                client.joinRoom(String.valueOf(roomDTO.getId()));
                client.sendEvent(ROOM_INFO_EVENT, roomDTO);
                socketIONamespace.getRoomOperations(String.valueOf(roomDTO.getId())).sendEvent(JOINED_USER_EVENT, userDTO);
            }catch (RoomException roomException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, roomException.getError(), null));
            }catch (UserException userException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, userException.getError(), null));
            }
        };
    }

    private DataListener<UserToRoomDTO> userStartTyping(){
        return ((client, data, ackSender) -> {
            userTyping(client, data, SERVER_TYPING_EVENT);
        });
    }

    public DataListener<UserToRoomDTO> userStopTyping(){
        return ((client, data, ackSender) -> {
            userTyping(client, data, SERVER_STOP_TYPING_EVENT);
        });
    }

    public DataListener<MessageDTO> sendUserMessage(){
        return ((client, data, ackSender) -> {
            try{
                roomService.addMessage(data);
                socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(SERVER_MESSAGE_EVENT, data);
            }catch (RoomException roomException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, roomException.getError(), null));
            }catch (UserException userException){
                client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, userException.getError(), null));
        }
        });
    }


    private DisconnectListener onDisconnected() {
        return client -> {
            client.disconnect();
            System.out.println("Client disconnected!" + client.getHandshakeData().getAddress());
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            System.out.println("Client connected!" + client.getHandshakeData().getAddress());
        };
    }

    private void userTyping(SocketIOClient client, UserToRoomDTO data, String event){
        try {
            UserDTO userDTO = userService.getUser(data.getUserId());
            HashMap<String, Long> userId = new HashMap<>();
            userId.put("userId", userDTO.getId());
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent(event,userId);
        }catch (UserException userException){
            client.sendEvent("server-error", new CustomResponseBody<>(ResultStatus.ERROR_STATUS, userException.getError(), null));
        }
    }

}
