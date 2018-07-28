package com.hedbanz.hedbanzAPI.timer;

import com.corundumstudio.socketio.BroadcastOperations;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.repository.PlayerRepository;
import com.hedbanz.hedbanzAPI.repository.RoomRepository;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.model.AfkWarning;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

import static com.hedbanz.hedbanzAPI.constant.PlayerStatus.AFK;
import static com.hedbanz.hedbanzAPI.constant.SocketEvents.*;

@Component
@Scope("prototype")
public class AfkTimerTask extends TimerTask {
    private final Logger log = LoggerFactory.getLogger("AFKTimerTask");
    //Time after which player will be kicked from room in ms
    private static final int ONE_MIN_IN_MS = 60000;

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
    private PlayerRepository playerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

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

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    @Override
    public void run() {
        Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            log.info("No such player");
            cancel();
            return;
        }
        if (player.getStatus() == AFK) {
            if (timeLeft == ONE_MIN_IN_MS) {
                User user = userRepository.findOne(userId);
                roomOperations.sendEvent(SERVER_PLAYER_AFK_WARNING, conversionService.convert(user, UserDto.class));
                log.info(SERVER_PLAYER_AFK_WARNING);
            } else if (timeLeft == ONE_MIN_IN_MS / 2) {
                User user = userRepository.findOne(userId);
                Room room = roomRepository.findOne(roomId);
                if(room == null)
                    cancel();
                AfkWarning warning = new AfkWarning(room.getName(), room.getId());
                FcmPush.FcmPushData<AfkWarning> fcmPushData =
                        new FcmPush.FcmPushData<>(NotificationMessageType.AFK_WARNING.getCode(), warning);
                FcmPush fcmPush = new FcmPush.Builder().setTo(user.getFcmToken())
                        .setNotification(new Notification("Afk warning",
                                "WARNING! In 30 secs you will be kicked out from the room with name" + room.getName()))
                        .setPriority("normal")
                        .setData(fcmPushData)
                        .build();
                fcmService.sendPushNotification(fcmPush);
                log.info("FCM Afk warning");
            } else if (timeLeft <= 0) {
                User user = userRepository.findOne(userId);
                Room room = roomRepository.findOne(roomId);
                if(room == null)
                    cancel();
                log.info(SERVER_KICKED_USER_EVENT);
                roomService.leaveFromRoom(user.getUserId(), room.getId());
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
                fcmService.sendPushNotification(fcmPush);
                cancel();
            }
            timeLeft -= period;
        } else {
            cancel();
            log.info("Player was returned!");
        }
    }
}
