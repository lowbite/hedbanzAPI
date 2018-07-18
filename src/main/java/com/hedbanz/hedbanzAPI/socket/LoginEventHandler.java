package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.transfer.LoginAnswerDto;
import com.hedbanz.hedbanzAPI.transfer.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginEventHandler {
    private final SocketIONamespace socketIONamespace;
    private final SocketIOServer server;

    private final UserRepository userRepository;

    @Autowired
    public LoginEventHandler(SocketIOServer server, UserRepository userRepository){
        this.server = server;
        this.socketIONamespace = server.addNamespace("/login");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener("checkLogin", LoginDto.class, onRecieved());
        this.userRepository = userRepository;
    }

    private DataListener<LoginDto> onRecieved() {
        return (client, data, ackSender) -> {
            User foundUserDTO = userRepository.findUserByLogin(data.getLogin());
            boolean isLoginAvailable = foundUserDTO == null;
            socketIONamespace.getBroadcastOperations().sendEvent("loginResult", new LoginAnswerDto(isLoginAvailable));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {};
    }

    private ConnectListener onConnected() {
        return client -> {};
    }
}
