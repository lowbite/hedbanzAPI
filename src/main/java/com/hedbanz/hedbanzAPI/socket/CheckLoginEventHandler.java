package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.transfer.LoginAvailabilityResponseDto;
import com.hedbanz.hedbanzAPI.transfer.LoginAvailabilityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hedbanz.hedbanzAPI.constant.SocketEvents.CLIENT_CHECK_LOGIN;
import static com.hedbanz.hedbanzAPI.constant.SocketEvents.SERVER_CHECK_LOGIN;

@Component
public class CheckLoginEventHandler {
    private final SocketIONamespace socketIONamespace;

    private final UserRepository userRepository;

    @Autowired
    public CheckLoginEventHandler(SocketIOServer server, UserRepository userRepository){
        this.socketIONamespace = server.addNamespace("/login");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener(CLIENT_CHECK_LOGIN, LoginAvailabilityDto.class, onRecieved());
        this.userRepository = userRepository;
    }

    private DataListener<LoginAvailabilityDto> onRecieved() {
        return (client, data, ackSender) -> {
            User foundUserDTO = userRepository.findUserByLogin(data.getLogin());
            boolean isLoginAvailable = foundUserDTO == null;
            socketIONamespace.getBroadcastOperations().sendEvent(SERVER_CHECK_LOGIN, new LoginAvailabilityResponseDto(isLoginAvailable));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {};
    }

    private ConnectListener onConnected() {
        return client -> {};
    }
}
