package com.hedbanz.hedbanzAPI.exception;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SocketExceptionListener implements ExceptionListener {
    private final Logger log = LoggerFactory.getLogger("SocketExceptionListener");
    @Override
    public void onEventException(Exception e, List<Object> list, SocketIOClient socketIOClient) {
        if(e instanceof RoomException) {
            socketIOClient.sendEvent("server-error", new CustomError(((RoomException) e).getCode(), e.getMessage()));
        }else if(e instanceof UserException){
            socketIOClient.sendEvent("server-error", new CustomError(((UserException) e).getCode(), e.getMessage()));
        }else{
            socketIOClient.sendEvent("server-error",new CustomError(500, "Internal server error"));
            log.error(e.getMessage());
        }
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient socketIOClient) {

    }

    @Override
    public void onConnectException(Exception e, SocketIOClient socketIOClient) {

    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        channelHandlerContext.fireChannelInactive();
        log.info(throwable.getMessage());
        return false;
    }
}
