package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.CustomResponseBody;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.RoomFilter;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.exception.UserException;
import com.hedbanz.hedbanzAPI.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoomController {
    @Autowired
    RoomService roomService;

    @RequestMapping(method = RequestMethod.PUT, value = "/rooms", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<Room> createRoom(@RequestBody Room room){
        Room newRoom =  roomService.addRoom(room);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, newRoom);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rooms/{page}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Room>> findAllRooms(@PathVariable("page") int page){
        List<Room> rooms = roomService.getAllRooms(page);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS,null, rooms);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rooms/{page}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<List<Room>> findRoomsByFilter(@RequestBody RoomFilter roomFilter, @PathVariable("page") int page){
        List<Room> rooms = roomService.getRoomsByFilter(roomFilter, page);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, rooms);
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> userError(UserException e){
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS, e.getError(), null);
    }

    @ExceptionHandler(RoomException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomResponseBody<User> roomError(RoomException e){
        return new CustomResponseBody<>(ResultStatus.ERROR_STATUS, e.getError(), null);
    }
}