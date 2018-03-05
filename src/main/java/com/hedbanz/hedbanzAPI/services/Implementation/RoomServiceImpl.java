package com.hedbanz.hedbanzAPI.services.Implementation;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.RoomFilter;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.UserToRoom;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exceptions.RoomException;
import com.hedbanz.hedbanzAPI.repositories.RoomRepository;
import com.hedbanz.hedbanzAPI.repositories.UserRepository;
import com.hedbanz.hedbanzAPI.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService{
    private final static int PAGE_SIZE = 8;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @CacheEvict(value = "rooms", allEntries = true)
    public Room addRoom(Room room){
        Iterator<User> userIterator = room.getUsers().iterator();
        User user = userIterator.next();
        Set<User> users = new HashSet<>();
        user = userRepository.findOne(user.getId());

        if(user == null)
            throw new RoomException(new CustomError(RoomError.WRONG_USER.getErrorCode(), RoomError.WRONG_USER.getErrorMessage()));

        users.add(user);
        room.setUsers(users);
        Room newRoom = roomRepository.saveAndFlush(room);

        if(newRoom == null)
            throw new RoomException(new CustomError(RoomError.DB_ERROR.getErrorCode(), RoomError.DB_ERROR.getErrorMessage()));


        newRoom.setCurrentPlayersNumber(1);
        return newRoom;
    }

    @Cacheable("rooms")
    public List<Room> getAllRooms(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber,PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> page = roomRepository.findAllRooms(pageable);
        List<Room> rooms = page.getContent();
        return rooms;
    }

    public List<Room> getRoomsByFilter(RoomFilter roomFilter, int pageNumber){
        List<Room> rooms = new ArrayList<>();
        Pageable pageable = new PageRequest(pageNumber,PAGE_SIZE, Sort.Direction.DESC, "id");
        //Searching by id
        if(roomFilter.getRoomName().charAt(0) == '#'){
            long roomId = Long.valueOf(roomFilter.getRoomName().substring(1, roomFilter.getRoomName().length()));
            if(roomFilter.isPrivate() == null )
                if(roomFilter.getMaxPlayers() == null)
                    rooms.addAll(roomRepository.findRoomById(roomId, pageable).getContent());
                else
                    rooms.addAll(roomRepository.findRoomByIdWithMaxPlayers(roomId, roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());
            else if(roomFilter.isPrivate() == false)
                if(roomFilter.getMaxPlayers() == null)
                    rooms.addAll(roomRepository.findRoomByIdWithoutPassword(roomId, pageable).getContent());
                else
                    rooms.addAll(roomRepository.findRoomByIdWithoutPasswordWithMaxPlayers(roomId, roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());
            else if(roomFilter.isPrivate() == true)
                if(roomFilter.getMaxPlayers() == null)
                    rooms.addAll(roomRepository.findRoomByIdWithPassword(roomId, pageable).getContent());
                else
                    rooms.addAll(roomRepository.findRoomByIdWithPasswordWithMaxPlayers(roomId, roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());
            return rooms;
        }

        //Searching by room name
        if(roomFilter.isPrivate() == null )
            if(roomFilter.getMaxPlayers() == null)
                rooms.addAll(roomRepository.findRoomByName(roomFilter.getRoomName(), pageable).getContent());
            else
                rooms.addAll(roomRepository.findRoomByNameWithMaxPlayers(roomFilter.getRoomName(), roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());
        else if(roomFilter.isPrivate() == false)
            if(roomFilter.getMaxPlayers() == null)
                rooms.addAll(roomRepository.findRoomByNameWithoutPassword(roomFilter.getRoomName(), pageable).getContent());
            else
                rooms.addAll(roomRepository.findRoomByNameWithoutPasswordWithMaxPlayers(roomFilter.getRoomName(), roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());
        else if(roomFilter.isPrivate() == true)
            if(roomFilter.getMaxPlayers() == null)
                rooms.addAll(roomRepository.findRoomByNameWithPassword(roomFilter.getRoomName(), pageable).getContent());
            else
                rooms.addAll(roomRepository.findRoomByNameWithPasswordWithMaxPlayers(roomFilter.getRoomName(), roomFilter.getMaxPlayers(), roomFilter.getMinPlayers(), pageable).getContent());

        return rooms;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public Room addUserToRoom(UserToRoom userToRoom){
        Room foundRoom = roomRepository.findOne(userToRoom.getRoomId());

        if(foundRoom.getUsers().size() == foundRoom.getMaxPlayers())
            throw new RoomException(new CustomError(RoomError.ROOM_FULL.getErrorCode(), RoomError.ROOM_FULL.getErrorMessage()));

        if(!foundRoom.getPassword().equals(userToRoom.getPassword()))
            throw new RoomException(new CustomError(RoomError.WRONG_PASSWORD.getErrorCode(),RoomError.WRONG_PASSWORD.getErrorMessage()));

        Set<User> users = foundRoom.getUsers();

        User user = userRepository.findOne(userToRoom.getUserId());

        if(user == null)
            throw new RoomException(new CustomError(RoomError.WRONG_USER.getErrorCode(), RoomError.WRONG_USER.getErrorMessage()));

        users.add(user);

        foundRoom = roomRepository.saveAndFlush(foundRoom);

        if(foundRoom == null)
            throw new RoomException(new CustomError(RoomError.DB_ERROR.getErrorCode(), RoomError.DB_ERROR.getErrorMessage()));

        foundRoom.setCurrentPlayersNumber(users.size());
        return  foundRoom;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public void leaveRoom(UserToRoom userToRoom){
        User user = userRepository.findOne(userToRoom.getUserId());

        if(user == null)
            throw new RoomException(new CustomError(RoomError.WRONG_USER.getErrorCode(), RoomError.WRONG_USER.getErrorMessage()));

        Room foundRoom = roomRepository.findOne(userToRoom.getRoomId());
        foundRoom.getUsers().remove(user);

        foundRoom = roomRepository.save(foundRoom);

        if(foundRoom == null)
            throw new RoomException(new CustomError(RoomError.DB_ERROR.getErrorCode(), RoomError.DB_ERROR.getErrorMessage()));
    }
}
