package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.PlayerService;
import com.hedbanz.hedbanzAPI.service.UserService;
import com.hedbanz.hedbanzAPI.transfer.LoginAvailabilityDto;
import com.hedbanz.hedbanzAPI.transfer.LoginAvailabilityResponseDto;
import com.hedbanz.hedbanzAPI.transfer.PlayerDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/socket-messaging")
public class SocketMessagingController {
    private final PlayerService playerService;
    private final ConversionService conversionService;
    private final UserService userService;
    private final MessageService messageService;

    public SocketMessagingController(PlayerService playerService, @Qualifier("APIConversionService") ConversionService conversionService,
                                     UserService userService, MessageService messageService) {
        this.playerService = playerService;
        this.conversionService = conversionService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/reconnect/user/{userId}/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerDto> playerReconnect(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) {
        Player player = playerService.setPlayerStatus(userId, roomId, PlayerStatus.ACTIVE);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(player, PlayerDto.class));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/disconnect/user/{userId}/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<PlayerDto> playerDisconnect(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) {
        Player player = playerService.getPlayer(userId, roomId);
        if (player.getStatus() != PlayerStatus.LEFT) {
            player = playerService.setPlayerStatus(userId, roomId, PlayerStatus.AFK);
            //messageService.deleteEmptyQuestions(roomId, userId);
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, conversionService.convert(player, PlayerDto.class));
    }

    @RequestMapping(value = "/login-availability", method = RequestMethod.POST)
    public ResponseBody<LoginAvailabilityResponseDto> isLoginAvailable(@RequestBody LoginAvailabilityDto loginAvailabilityDto){
        if(userService.getUserByLogin(loginAvailabilityDto.getLogin()) == null)
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, new LoginAvailabilityResponseDto(true));
        else
            return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, new LoginAvailabilityResponseDto(false));
    }
}
