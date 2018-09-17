package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.transfer.InviteDto;
import com.hedbanz.hedbanzAPI.transfer.PushMessageDto;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FriendController {
    private final UserService userService;
    private final FcmService fcmService;
    private final RoomService roomService;

    @Autowired
    public FriendController(UserService userService, FcmService fcmService, RoomService roomService) {
        this.userService = userService;
        this.fcmService = fcmService;
        this.roomService = roomService;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<String> friendshipRequest(@PathVariable("userId") long userId,
                                                  @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        User user = userService.getUser(userId);
        User friend = userService.getUser(friendId);
        try {
            if (!TextUtils.isEmpty(friend.getFcmToken())) {
                FcmPush fcmPush = new FcmPush.Builder().setTo(friend.getFcmToken())
                        .setNotification(new Notification("New friend request!",
                                "Player " + friend.getLogin() + " wants to add to his friend list."))
                        .setData(new FcmPush.FcmPushData<>(NotificationMessageType.FRIEND.getCode(),
                                new PushMessageDto.Builder().setSenderName(user.getLogin()).build()))
                        .setPriority("normal")
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.create(e, UserError.CANT_SEND_FRIENDSHIP_REQUEST);
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<String> friendshipAccept(@PathVariable("userId") long userId,
                                                 @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<String> declineFriendshipRequest(@PathVariable("userId") long userId,
                                                         @PathVariable("friendId") long friendId) {
        userService.declineFriendship(friendId, userId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<String> deleteFriendship(@PathVariable("userId") long userId,
                                                 @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<List<Friend>> getFriendList(@PathVariable("userId") String userId) {
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, userService.getUserFriends(Long.valueOf(userId)));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/friends/invite")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<InviteDto> inviteFriendIntoRoom(@RequestBody InviteDto inviteDto) {
        roomService.checkPlayerInRoom(inviteDto.getSenderId(), inviteDto.getRoomId());
        User senderUser = userService.getUser(inviteDto.getSenderId());
        for (Long userId : inviteDto.getInvitedUserIds()) {
            User user = userService.getUser(userId);
            if (!TextUtils.isEmpty(user.getFcmToken())) {
                userService.addInvite(userId, inviteDto.getRoomId());
                Room room = roomService.getRoom(inviteDto.getRoomId());
                PushMessageDto pushMessageDto = new PushMessageDto.Builder()
                        .setSenderName(senderUser.getLogin())
                        .setRoomName(room.getName())
                        .setRoomId(inviteDto.getRoomId())
                        .build();
                FcmPush.FcmPushData<PushMessageDto> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.INVITE.getCode(), pushMessageDto);
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(user.getFcmToken())
                        .setNotification(new Notification("Invite to room", "Friend inviting you to room"))
                        .setData(fcmPushData)
                        .setPriority("normal")
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<List<Friend>> getUserFriendsWithInvites(@PathVariable("userId") long userId,
                                                                @PathVariable("roomId") long roomId) {
        roomService.checkPlayerInRoom(userId, roomId);
        List<Friend> friends = userService.getAcceptedFriendsInRoom(userId, roomId);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, friends);
    }
}
