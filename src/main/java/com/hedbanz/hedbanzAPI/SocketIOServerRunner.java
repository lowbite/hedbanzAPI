package com.hedbanz.hedbanzAPI;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SocketIOServerRunner implements CommandLineRunner {
    private SocketIOServer server;

    @Autowired
    public SocketIOServerRunner(SocketIOServer server){
        this.server = server;
    }

    @Override
    public void run(String... strings) throws Exception {
        //server.start();
    }
}
