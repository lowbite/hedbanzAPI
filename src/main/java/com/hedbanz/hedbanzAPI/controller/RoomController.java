package com.hedbanz.hedbanzAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.*;
import com.hedbanz.hedbanzAPI.utils.MessageHistoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public RoomController(FcmService fcmService, UserService userService, RoomService roomService, MessageService messageService,
                          PlayerService playerService, @Qualifier("APIConversionService") ConversionService conversionService) {
        this.fcmService = fcmService;
        this.userService = userService;
        this.roomService = roomService;
        this.messageService = messageService;
        this.playerService = playerService;
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        Room createdRoom = roomService.addRoom(conversionService.convert(roomDto, Room.class), roomDto.getUserId());
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(createdRoom, RoomDto.class));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Message>> deleteRoom(@PathVariable("roomId") long roomId) {
        roomService.deleteRoom(roomId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{pageNumber}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<Map<String, List<RoomDto>>> findAllRooms(@PathVariable("pageNumber") int page,
                                                                       @PathVariable("userId") Long userId) {
        Map<String, List<RoomDto>> rooms = new HashMap<>();
        List<Room> allRooms = roomService.getAllRooms(page);
        rooms.put("allRooms", allRooms.stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList()));
        if (page == 0) {
            List<RoomDto> activeRooms = roomService.getActiveRooms(userId)
                    .stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList());
            rooms.put("activeRooms", activeRooms);
        }
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{pageNumber}/user/{userId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<Map<String, List<RoomDto>>> findRoomsByFilter(@RequestBody RoomFilter roomFilter,
                                                                            @PathVariable("pageNumber") int page,
                                                                            @PathVariable("userId") Long userId) {
        Map<String, List<RoomDto>> rooms = new HashMap<>();
        List<Room> allRooms = roomService.getRoomsByFilter(roomFilter, page);
        rooms.put("allRooms", allRooms.stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList()));
        if (page == 0) {
            List<RoomDto> activeRooms = roomService.getActiveRoomsByFilter(roomFilter, userId)
                    .stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList());
            rooms.put("activeRooms", activeRooms);
        }
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{roomId}/messages/{pageNumber}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<MessageDto>> findAllMessages(@PathVariable("roomId") long roomId, @PathVariable("pageNumber") int pageNumber) {
        List<Message> messages = messageService.getAllMessages(roomId, pageNumber);
        List<Player> players = playerService.getPlayers(roomId);
        List<MessageDto> resultMessages = MessageHistoryUtil.convertToDto(messages, players, conversionService);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, resultMessages);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/password")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserToRoomDto> checkPassword(@RequestBody UserToRoomDto userToRoomDto) {
        roomService.checkRoomPassword(userToRoomDto.getRoomId(), userToRoomDto.getPassword());
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/invite")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<InviteDto> inviteFriendIntoRoom(@RequestBody InviteDto inviteDto) {
        roomService.checkPlayerInRoom(inviteDto.getSenderId(), inviteDto.getSenderId());
        Room room = roomService.addUserToRoom(inviteDto.getInvitedUserId(), inviteDto.getRoomId(), inviteDto.getPassword());
        User user = userService.getUser(inviteDto.getInvitedUserId());
        FcmPush.FcmPushData fcmPushData = new FcmPush.FcmPushData(NotificationMessageType.INVITE.getCode(), null);
        FcmPush fcmPush = new FcmPush.Builder()
                .setTo(user.getFcmToken())
                .setNotification(new Notification("Invite to room", "Friend inviting you to room " + room.getName()))
                .setData(fcmPushData)
                .setPriority("normal")
                .build();
        fcmService.sendPushNotification(fcmPush);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}