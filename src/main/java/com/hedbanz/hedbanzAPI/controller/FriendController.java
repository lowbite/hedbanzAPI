package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.*;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.transfer.InviteDto;
import com.hedbanz.hedbanzAPI.transfer.UserToRoomDto;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FriendController {
    @Autowired
    private UserService userService;
    @Autowired
    private FcmService fcmService;
    @Autowired
    private RoomService roomService;

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> friendshipRequest(@PathVariable("userId") long userId,
                                                        @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        User user = userService.getUser(userId);
        User friend = userService.getUser(friendId);
        if(!TextUtils.isEmpty(friend.getFcmToken())) {
            FcmPush fcmPush = new FcmPush.Builder().setTo(friend.getFcmToken())
                    .setNotification(new Notification("New friend request!",
                            "Player " + friend.getLogin() + " wants to add to his friend list."))
                    .setData(new FcmPush.FcmPushData<>(NotificationMessageType.FRIEND.getCode(),
                            MessageNotification.Builder().setSenderName(user.getLogin()).build()))
                    .setPriority("normal")
                    .build();
            fcmService.sendPushNotification(fcmPush);
        }
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> friendshipAccept(@PathVariable("userId") long userId,
                                                       @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> declineFriendshipRequest(@PathVariable("userId") long userId,
                                                               @PathVariable("friendId") long friendId) {
        userService.declineFriendship(friendId, userId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<String> deleteFriendship(@PathVariable("userId") long userId,
                                                       @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Friend>> getFriendList(@PathVariable("userId") String userId) {
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, userService.getUserFriends(Long.valueOf(userId)));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/friends/invite")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<InviteDto> inviteFriendIntoRoom(@RequestBody InviteDto inviteDto) {
        roomService.checkPlayerInRoom(inviteDto.getSenderId(), inviteDto.getRoomId());
        for (Long userId : inviteDto.getInvitedUserId()) {
            User user = userService.getUser(userId);
            if (!TextUtils.isEmpty(user.getFcmToken())) {
                userService.addInvite(userId, inviteDto.getRoomId());
                UserToRoomDto userToRoomDto = new UserToRoomDto.Builder()
                        .setUserId(userId)
                        .setRoomId(inviteDto.getRoomId())
                        .setPassword(inviteDto.getPassword())
                        .build();
                FcmPush.FcmPushData<UserToRoomDto> fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.INVITE.getCode(), userToRoomDto);
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(user.getFcmToken())
                        .setNotification(new Notification("Invite to room", "Friend inviting you to room"))
                        .setData(fcmPushData)
                        .setPriority("normal")
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
        }
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Friend>> getUserFriendsWithInvites(@PathVariable("userId") long userId,
                                                                      @PathVariable("roomId") long roomId){
        roomService.checkPlayerInRoom(userId, roomId);
        List<Friend> friends = userService.getAcceptedFriendsInRoom(userId, roomId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, friends);
    }
}
