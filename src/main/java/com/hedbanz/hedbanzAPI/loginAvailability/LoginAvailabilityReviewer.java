package com.hedbanz.hedbanzAPI.loginAvailability;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginAvailabilityReviewer {
    private final SocketIONamespace socketIONamespace;
    private final SocketIOServer server;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public LoginAvailabilityReviewer(SocketIOServer server){
        this.server = server;
        this.socketIONamespace = server.addNamespace("/login");
        this.socketIONamespace.addConnectListener(onConnected());
        this.socketIONamespace.addDisconnectListener(onDisconnected());
        this.socketIONamespace.addEventListener("checkLogin", LoginAvailabilityReviewerReceive.class, onRecieved());
    }

    private DataListener<LoginAvailabilityReviewerReceive> onRecieved() {
        return (client, data, ackSender) -> {
            User foundUser = userRepository.findUserByLogin(data.getLogin());
            boolean isLoginAvailable = foundUser == null;
            socketIONamespace.getBroadcastOperations().sendEvent("loginResult", new LoginAvailabilityReviewerAnswer(isLoginAvailable));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {};
    }

    private ConnectListener onConnected() {
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
        };
    }
}
