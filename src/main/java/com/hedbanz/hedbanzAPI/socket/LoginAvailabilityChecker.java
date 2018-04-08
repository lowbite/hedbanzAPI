package com.hedbanz.hedbanzAPI.socket;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.repository.CRUDUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginAvailabilityChecker {
    private final SocketIONamespace socketIONamespace;
    private final SocketIOServer server;

    @Autowired
    private CRUDUserRepository CRUDUserRepository;

    @Autowired
    public LoginAvailabilityChecker(SocketIOServer server){
        this.server = server;
        this.socketIONamespace = server.addNamespace("/login");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener("checkLogin", LoginAvailabilityReceiveMessage.class, onRecieved());
    }

    private DataListener<LoginAvailabilityReceiveMessage> onRecieved() {
        return new DataListener<LoginAvailabilityReceiveMessage>() {
            @Override
            public void onData(SocketIOClient client, LoginAvailabilityReceiveMessage data, AckRequest ackSender) throws Exception {
                User foundUserDTO = CRUDUserRepository.findUserByLogin(data.getLogin());
                boolean isLoginAvailable = foundUserDTO == null;
                socketIONamespace.getBroadcastOperations().sendEvent("loginResult", new LoginAvailabilityAnswer(isLoginAvailable));
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                
            }
        };
    }

    private ConnectListener onConnected() {
        return new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                HandshakeData handshakeData = client.getHandshakeData();
            }
        };
    }
}
