package com.hedbanz.hedbanzAPI.transfer;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.SocketIOClient;
import com.hedbanz.hedbanzAPI.socket.RoomEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetWordAckCallback extends AckCallback {
    private final Logger log = LoggerFactory.getLogger("SetWordAckCallback");
    private static final String SERVER_SET_PLAYER_WORD_EVENT = "server-set-word";
    private SocketIOClient client;
    private Long wordReceiverId;

    public SetWordAckCallback(Class resultClass, int timeout) {
        super(resultClass, timeout);
    }

    @Override
    public void onSuccess(Object o) {
        log.info("Ack Callback response:" + o);
    }

    public void onTimeout() {
        if(client.isChannelOpen()) {
            log.info("Ack Callback timeout!");
            client.sendEvent(SERVER_SET_PLAYER_WORD_EVENT, this, new WordDto.WordDTOBuilder().setWordReceiverId(wordReceiverId)
                    .createWordDTO());
        }else{
            RoomEventListener.sendWordEventClientsList.add(new WordDto.WordDTOBuilder()
                                                                        .setSenderId(client.get("userId"))
                                                                        .setWordReceiverId(wordReceiverId)
                                                                        .createWordDTO());
        }
    }

    public SocketIOClient getClient() {
        return client;
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    public Long getWordReceiverId() {
        return wordReceiverId;
    }

    public void setWordReceiverId(Long wordReceiverId) {
        this.wordReceiverId = wordReceiverId;
    }
}
