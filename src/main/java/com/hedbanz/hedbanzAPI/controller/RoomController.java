package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.*;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.utils.PlayersUtil;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/rooms")
public class RoomController {
    private final FcmService fcmService;
    private final UserService userService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final PlayerService playerService;
    private final ConversionService conversionService;

    @Autowired
    public RoomController(FcmService fcmService, UserService userService, RoomService roomService,
                          MessageService messageService, PlayerService playerService,
                          @Qualifier("APIConversionService") ConversionService conversionService) {
        this.fcmService = fcmService;
        this.userService = userService;
        this.roomService = roomService;
        this.messageService = messageService;
        this.playerService = playerService;
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{roomId}")
    @ResponseStatus(OK)
    public ResponseBody<RoomDto> getRoom(@PathVariable("roomId") long roomId) {
        Room room = roomService.getRoom(roomId);
        List<Player> players = playerService.getPlayersFromRoom(roomId);
        List<PlayerDto> playerDtos = players.stream()
                .map(player -> conversionService.convert(player, PlayerDto.class))
                .collect(Collectors.toList());
        RoomDto roomDto = conversionService.convert(room, RoomDto.class);
        roomDto.setPlayers(playerDtos);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, roomDto);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(OK)
    public ResponseBody<RoomDto> addRoom(@RequestBody RoomDto roomDto) {
        Room createdRoom = roomService.addRoom(conversionService.convert(roomDto, Room.class), roomDto.getUserId());
        List<String> fcmTokens = userService.getAllFcmTokens();
        Notification notification = new Notification(
                "New room!", "Room " + createdRoom.getName() + " is available to join"
        );
        FcmPush.FcmPushData<RoomDto> fcmPushData = new FcmPush.FcmPushData<>(
                NotificationMessageType.NEW_ROOM_CREATED.getCode(),
                conversionService.convert(createdRoom, RoomDto.class)
        );
        FcmPush fcmPush = new FcmPush.Builder().setNotification(notification)
                .setPriority("normal")
                .setData(fcmPushData)
                .build();
        fcmService.sendPushNotificationsToUsers(fcmPush, fcmTokens);
        messageService.addRoomEventMessage(MessageType.WAITING_FOR_PLAYERS, createdRoom.getId());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(createdRoom, RoomDto.class));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{roomId}")
    @ResponseStatus(OK)
    public ResponseBody<List<Message>> deleteRoom(@PathVariable("roomId") long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{pageNumber}/user/{userId}")
    @ResponseStatus(OK)
    public ResponseBody<Map<String, List<RoomDto>>> getAllRooms(@PathVariable("pageNumber") int page,
                                                                @PathVariable("userId") Long userId) {
        Map<String, List<RoomDto>> rooms = new HashMap<>();
        List<Room> allRooms = roomService.getAllRooms(page);
        rooms.put("allRooms", allRooms.stream()
                .map(room -> conversionService.convert(room, RoomDto.class))
                .collect(Collectors.toList()));
        if (page == 0) {
            List<RoomDto> activeRooms = roomService.getActiveRooms(userId)
                    .stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList());
            rooms.put("activeRooms", activeRooms);
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{pageNumber}/user/{userId}", consumes = "application/json")
    @ResponseStatus(OK)
    public ResponseBody<Map<String, List<RoomDto>>> getRoomsByFilter(@RequestBody RoomFilter roomFilter,
                                                                     @PathVariable("pageNumber") int page,
                                                                     @PathVariable("userId") Long userId) {
        Map<String, List<RoomDto>> rooms = new HashMap<>();
        List<Room> allRooms = roomService.getRoomsByFilter(roomFilter, page);
        rooms.put("allRooms", allRooms.stream()
                .map(room -> conversionService.convert(room, RoomDto.class))
                .collect(Collectors.toList()));
        if (page == 0) {
            List<RoomDto> activeRooms = roomService.getActiveRoomsByFilter(roomFilter, userId)
                    .stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList());

            rooms.put("activeRooms", activeRooms);
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{roomId}/messages/{pageNumber}")
    @ResponseStatus(OK)
    public ResponseBody<List<MessageDto>> findAllMessages(@PathVariable("roomId") long roomId,
                                                          @PathVariable("pageNumber") int pageNumber) {
        List<Message> messages = messageService.getAllMessages(roomId, pageNumber);
        List<MessageDto> resultMessages = messages.stream().map(message -> {
            if (message.getType() == MessageType.USER_QUESTION) {
                return conversionService.convert(message, QuestionDto.class);
            } else if (message.getType() == MessageType.WORD_SETTING) {
                SetWordDto setWordDto = conversionService.convert(message, SetWordDto.class);
                Player wordSetter = playerService.getPlayer(setWordDto.getSenderUser().getId(), setWordDto.getRoomId());
                Player wordReceiver = playerService.getPlayer(wordSetter.getWordReceiverUserId(), setWordDto.getRoomId());
                setWordDto.setWordReceiverUser(conversionService.convert(wordReceiver.getUser(), UserDto.class));
                setWordDto.setWord(wordReceiver.getWord());
                return setWordDto;
            } else {
                return conversionService.convert(message, MessageDto.class);
            }
        }).collect(Collectors.toList());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultMessages);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/password")
    @ResponseStatus(OK)
    public ResponseBody<UserToRoomDto> checkPassword(@RequestBody UserToRoomDto userToRoomDto) {
        roomService.checkRoomPassword(userToRoomDto.getRoomId(), userToRoomDto.getPassword());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/join-user")
    @ResponseStatus(OK)
    public ResponseBody<RoomDto> joinUserToRoom(@RequestBody UserToRoomDto userToRoomDto) {
        roomService.addUserToRoom(userToRoomDto.getUserId(),
                userToRoomDto.getRoomId(), userToRoomDto.getPassword());
        messageService.addPlayerEventMessage(
                MessageType.JOINED_USER, userToRoomDto.getUserId(), userToRoomDto.getRoomId()
        );
        List<Friend> friends = userService.getUserFriends(userToRoomDto.getUserId());
        Room room = roomService.getRoom(userToRoomDto.getRoomId());
        RoomDto resultRoom = conversionService.convert(room, RoomDto.class);
        resultRoom.setPlayers(room.getPlayers().stream()
                .map(player -> conversionService.convert(player, PlayerDto.class))
                .collect(Collectors.toList()));
        for (Friend friend : friends) {
            for (PlayerDto player : resultRoom.getPlayers()) {
                if (friend.getId().equals(player.getUserId())) {
                    if (friend.getIsAccepted())
                        player.setIsFriend(true);
                    else if (friend.getIsPending())
                        player.setIsPending(true);
                }
            }
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultRoom);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/leave")
    @ResponseStatus(OK)
    public ResponseBody<UserDto> leaveFromRoom(@RequestBody UserToRoomDto userToRoomDto) {
        messageService.addPlayerEventMessage(MessageType.LEFT_USER, userToRoomDto.getUserId(), userToRoomDto.getRoomId());
        User user = userService.getUser(userToRoomDto.getUserId());
        Player player = playerService.getPlayer(user.getUserId(), userToRoomDto.getRoomId());
        roomService.leaveUserFromRoom(userToRoomDto.getUserId(), userToRoomDto.getRoomId());
        Room room = roomService.getRoom(userToRoomDto.getRoomId());
        if (room.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            Player lastPlayer = PlayersUtil.getLastPlayer(room.getPlayers());
            if (lastPlayer != null && !TextUtils.isEmpty(lastPlayer.getUser().getFcmToken())) {
                Notification notification = new Notification("Last player in room!",
                        "You are the last player in room");
                FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(
                        NotificationMessageType.LAST_PLAYER.getCode(),
                        new PushMessageDto.Builder()
                                .setRoomId(room.getId())
                                .setRoomName(room.getName())
                                .build());
                FcmPush fcmPush = new FcmPush.Builder().setNotification(notification)
                        .setTo(lastPlayer.getUser().getFcmToken())
                        .setPriority("normal")
                        .setData(fcmPushData)
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
            if (TextUtils.isEmpty(playerService.getPlayer(player.getWordReceiverUserId(), room.getId()).getWord())) {
                messageService.deleteSettingWordMessage(room.getId(), user.getUserId());
            }

            if (room.getGameStatus() == GameStatus.SETTING_WORDS) {
                messageService.addRoomEventMessage(MessageType.UPDATE_USERS_INFO, room.getId());
            }

            /*if(room.getGameStatus() == GameStatus.GUESSING_WORDS) {
                messageService.deleteEmptyQuestions(room.getId(), user.getUserId());
            }*/
        }
        if (room.getCurrentPlayersNumber() == 0 || PlayersUtil.isPlayersAbsent(room.getPlayers())) {
            roomService.deleteRoom(room.getId());
        }

        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(user, UserDto.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "{roomId}/player/{userId}")
    @ResponseStatus(OK)
    public ResponseBody<PlayerDto> getPlayer(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) {
        Player player = playerService.getPlayer(userId, roomId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(player, PlayerDto.class));
    }

    @PostMapping(value = "{roomId}/events/set-word-entity")
    @ResponseStatus(OK)
    public ResponseBody<List<SetWordDto>> getSetWordMessages(@RequestBody List<PlayerDto> playerDtos,
                                                             @PathVariable("roomId") long roomId) {
        List<SetWordDto> result = new ArrayList<>();
        for (PlayerDto playerDto : playerDtos) {
            Message message = messageService.getSettingWordMessage(roomId, playerDto.getUserId());
            SetWordDto setWordDto = conversionService.convert(message, SetWordDto.class);
            Player wordSetter = playerService.getPlayer(setWordDto.getSenderUser().getId(), setWordDto.getRoomId());
            Player wordReceiver = playerService.getPlayer(wordSetter.getWordReceiverUserId(), setWordDto.getRoomId());
            setWordDto.setWordReceiverUser(conversionService.convert(wordReceiver.getUser(), UserDto.class));
            result.add(setWordDto);
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{roomId}/players")
    @ResponseStatus(OK)
    public ResponseBody<List<PlayerDto>> getPlayers(@PathVariable("roomId") long roomId) {
        List<Player> players = playerService.getPlayersFromRoom(roomId);
        List<PlayerDto> playerDtos = players.stream()
                .map(player -> conversionService.convert(player, PlayerDto.class)).collect(Collectors.toList());
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, playerDtos);
    }


}