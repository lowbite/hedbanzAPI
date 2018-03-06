package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.UserToRoom;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    private final SocketIONamespace socketIONamespace;

    @Autowired
    public ChatEventListener(SocketIOServer server){
        this.socketIONamespace = server.addNamespace("/game");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener("join-room", UserToRoom.class, joinUserToRoom());
        this.socketIONamespace.addEventListener("leave-room", UserToRoom.class, leaveUserFromRoom());
    }

    private DataListener<UserToRoom> leaveUserFromRoom() {
        return (client, data, ackSender) -> {
            roomService.leaveRoom(data);
            User user = userService.getUser(data.getUserId());
            client.leaveRoom(String.valueOf(data.getRoomId()));
            socketIONamespace.getRoomOperations(String.valueOf(data.getRoomId())).sendEvent("left-user", user.getLogin());
        };
    }

    private DataListener<UserToRoom> joinUserToRoom() {
        return (client, data, ackSender) -> {
            Room room = roomService.addUserToRoom(data);
            User user = userService.getUser(data.getUserId());
            client.joinRoom(String.valueOf(room.getId()));
            client.sendEvent("joined-room", room);
            socketIONamespace.getRoomOperations(String.valueOf(room.getId())).sendEvent("joined-user", user.getLogin());
        };
    }


    private DisconnectListener onDisconnected() {
        System.out.println("Client disconnected!");
        return client -> {
        };
    }

    private ConnectListener onConnected() {
        System.out.println("Client connected!");
        return client -> {
        };
    }

}
