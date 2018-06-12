package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.transfer.*;
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
    private final RoomService roomService;
    private final MessageService messageService;
    private final ConversionService conversionService;

    @Autowired
    public RoomController(RoomService roomService, MessageService messageService,
                          @Qualifier("APIConversionService") ConversionService conversionService) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<RoomDto> createRoom(@RequestBody RoomDto roomDto){
        Room createdRoom =  roomService.addRoom(conversionService.convert(roomDto, Room.class), roomDto.getUserId());
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, conversionService.convert(createdRoom, RoomDto.class));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Message>> deleteRoom(@PathVariable("roomId") long roomId){
        roomService.deleteRoom(roomId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{pageNumber}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<Map<String, List<RoomDto>>> findAllRooms(@PathVariable("pageNumber") int page, @PathVariable("userId") Long userId){
        Map<String, List<RoomDto>> rooms = new HashMap<>();
        List<Room> allRooms = roomService.getAllRooms(page);
        rooms.put("allRooms", allRooms.stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList()));
        if(page == 0) {
            List<RoomDto> activeRooms = roomService.getActiveRooms(userId).stream().map(room -> conversionService.convert(room, RoomDto.class)).collect(Collectors.toList());
            rooms.put("activeRooms", activeRooms);
        }
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, rooms);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{pageNumber}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<RoomDto>> findRoomsByFilter(@RequestBody RoomFilterDto roomFilterDto, @PathVariable("pageNumber") int page){
        List<Room> rooms = roomService.getRoomsByFilter(roomFilterDto, page);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms.stream().map(room -> conversionService.convert(room, RoomDto.class))
                                                                                    .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{roomId}/messages/{pageNumber}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<MessageDto>> findAllMessages(@PathVariable("roomId") long roomId, @PathVariable("pageNumber") int pageNumber){
        List<MessageDto> messages = messageService.getAllMessages(roomId, pageNumber);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, messages);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/password")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserToRoomDto> checkPassword(@RequestBody UserToRoomDto userToRoomDto){
        roomService.checkRoomPassword(userToRoomDto.getRoomId(), userToRoomDto.getPassword());
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}