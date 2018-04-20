package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.exception.UserException;
import com.hedbanz.hedbanzAPI.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rooms", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO){
        RoomDTO createdRoomDTO =  roomService.addRoom(roomDTO);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, createdRoomDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/rooms")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Message>> deleteRoom(@RequestParam("roomId") long roomId){
        roomService.deleteRoom(roomId);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rooms/{page}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<RoomDTO>> findAllRooms(@PathVariable("page") int page){
        List<RoomDTO> roomDTOS = roomService.getAllRooms(page);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, roomDTOS);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rooms/{page}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<RoomDTO>> findRoomsByFilter(@RequestBody RoomFilterDTO roomFilterDTO, @PathVariable("page") int page){
        List<RoomDTO> roomDTOS = roomService.getRoomsByFilter(roomFilterDTO, page);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, roomDTOS);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rooms/messages")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<MessageDTO>> findAllMessages(@RequestParam("roomId") long roomId, @RequestParam("page") int pageNumber){
        List<MessageDTO> messages = roomService.getAllMessages(roomId, pageNumber);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, messages);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rooms/password")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<UserToRoomDTO> checkPassword(@RequestBody UserToRoomDTO userToRoomDTO){
        roomService.checkRoomPassword(userToRoomDTO);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}