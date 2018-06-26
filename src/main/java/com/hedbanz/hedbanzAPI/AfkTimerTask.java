package com.hedbanz.hedbanzAPI;

import com.corundumstudio.socketio.BroadcastOperations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.repository.CrudPlayerRepository;
import com.hedbanz.hedbanzAPI.repository.CrudRoomRepository;
import com.hedbanz.hedbanzAPI.repository.CrudUserRepository;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.transfer.AfkWarning;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

import static com.hedbanz.hedbanzAPI.constant.PlayerStatus.AFK;

@Component
@Scope("prototype")
public class AfkTimerTask extends TimerTask {
    private final Logger log = LoggerFactory.getLogger("AFKTimerTask");
    //Time to playerId will be kicked from room in ms
    private static final int ONE_MIN_IN_MS = 60000;
    private static final String SERVER_PLAYER_AFK_WARNING = "server-player-afk-warning";
    private static final String SERVER_KICKED_USER_EVENT = "server-kicked-user";

    private int timeLeft;
    private BroadcastOperations roomOperations;


    private Long userId;
    private Long roomId;
    private Long period;
    @Autowired
    private RoomService roomService;
    @Autowired
    private FcmService fcmService;
    @Autowired
    @Qualifier("APIConversionService")
    private ConversionService conversionService;
    @Autowired
    private CrudPlayerRepository crudPlayerRepository;
    @Autowired
    private CrudUserRepository crudUserRepository;
    @Autowired
    private CrudRoomRepository crudRoomRepository;

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setRoomOperations(BroadcastOperations roomOperations) {
        this.roomOperations = roomOperations;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRoomId(Long roomId){
        this.roomId = roomId;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    @Override
    public void run() {
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if(player == null){
            log.info("No such player");
            cancel();
            return;
        }
        log.info("Player status " + player.getStatus());
        if(player.getStatus() == AFK) {
            if (timeLeft == ONE_MIN_IN_MS) {
                User user = crudUserRepository.findOne(userId);
                roomOperations.sendEvent(SERVER_PLAYER_AFK_WARNING, conversionService.convert(user, UserDto.class));
                log.info(SERVER_PLAYER_AFK_WARNING);
            }else if (timeLeft == ONE_MIN_IN_MS / 2) {
                User user = crudUserRepository.findOne(userId);
                Room room = crudRoomRepository.findOne(roomId);
                AfkWarning warning = new AfkWarning(room.getName(), room.getId());
                FcmPush.FcmPushData<AfkWarning> fcmPushData =
                        new FcmPush.FcmPushData<>(NotificationMessageType.AFK_WARNING.getCode(), warning);
                FcmPush fcmPush = new FcmPush.Builder().setTo(user.getFcmToken())
                        .setNotification(new Notification("Afk warning",
                                "WARNING! In 30 secs you will be kicked out from the room with name" + room.getName()))
                        .setPriority("normal")
                        .setData(fcmPushData)
                        .build();
                //TODO Check why try catch need here
                try {
                    fcmService.sendPushNotification(fcmPush);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                log.info("FCM Afk warning");
            } else if (timeLeft <= 0) {
                User user = crudUserRepository.findOne(userId);
                Room room = crudRoomRepository.findOne(roomId);
                log.info("Player was kicked!");
                roomService.leaveFromRoom(user.getId(), room.getId());
                roomOperations.sendEvent(SERVER_KICKED_USER_EVENT, conversionService.convert(user, UserDto.class));
                AfkWarning warning = new AfkWarning(room.getName(), room.getId());
                FcmPush.FcmPushData<AfkWarning> fcmPushData =
                        new FcmPush.FcmPushData<>(NotificationMessageType.USER_KICKED.getCode(), warning);
                FcmPush fcmPush = new FcmPush.Builder().setTo(user.getFcmToken())
                        .setNotification(new Notification("Afk warning",
                                "You were kicked out from room with name" + room.getName()))
                        .setPriority("normal")
                        .setData(fcmPushData)
                        .build();
                //TODO Check why try catch need here
                try {
                    fcmService.sendPushNotification(fcmPush);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                cancel();
            }
            timeLeft -= period;
        }else{
            cancel();
            log.info("Player was returned!");
        }
    }
}
