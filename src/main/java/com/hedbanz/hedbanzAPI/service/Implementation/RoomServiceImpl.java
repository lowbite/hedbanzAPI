package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CRUDRoomRepository;
import com.hedbanz.hedbanzAPI.repository.CRUDUserRepository;
import com.hedbanz.hedbanzAPI.repository.RoomRepositoryFunctional;
import com.hedbanz.hedbanzAPI.service.RoomService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService{
    @Qualifier("APIConversionService")
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private CRUDRoomRepository CRUDRoomRepository;

    @Autowired
    private RoomRepositoryFunctional roomRepositoryFunctional;

    @Autowired
    private CRUDUserRepository CRUDUserRepository;

    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDTO addRoom(RoomDTO roomDTO){
        Room room = conversionService.convert(roomDTO, Room.class);
        Iterator<UserDTO> userIterator = roomDTO.getUsers().iterator();
        UserDTO userDTO= userIterator.next();
        User user = CRUDUserRepository.findOne(userDTO.getId());

        if(room.getName() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if(user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        if(room.getPassword().equals(""))
            room.setPassword(null);

        room.addUser(user);
        room.setCurrentPlayersNumber(0);
        room.setRoomAdmin(user.getId());
        CRUDRoomRepository.saveAndFlush(room);

        room.setCurrentPlayersNumber(1);
        return conversionService.convert(room, RoomDTO.class);
    }

    public void deleteRoom(long roomId){
        CRUDRoomRepository.delete(roomId);
    }

    public RoomDTO getRoom(long roomId){
        return conversionService.convert(CRUDRoomRepository.findOne(roomId), RoomDTO.class);
    }

    @Cacheable("rooms")
    public List<RoomDTO> getAllRooms(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<RoomDTO> page = CRUDRoomRepository.findAllRooms(pageable);

        return page.getContent();
    }

    public List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber){
        return roomRepositoryFunctional.findRoomsByFilter(roomFilterDTO,pageNumber,Constants.PAGE_SIZE);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO){
        Room foundRoom = CRUDRoomRepository.findOne(userToRoomDTO.getRoomId());

        if(foundRoom.getUserCount() == foundRoom.getMaxPlayers())
            throw ExceptionFactory.create(RoomError.ROOM_FULL);

        if(!TextUtils.isEmpty(foundRoom.getPassword())&&!TextUtils.isEmpty(userToRoomDTO.getPassword()))
            if(!foundRoom.getPassword().equals(userToRoomDTO.getPassword()))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);

        User userDTO = CRUDUserRepository.findOne(userToRoomDTO.getUserId());
        if(userDTO == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);

        if(!foundRoom.addUser(userDTO)){
            throw ExceptionFactory.create(RoomError.ALREADY_IN_ROOM);
        }
        CRUDRoomRepository.saveAndFlush(foundRoom);
        foundRoom = CRUDRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null)
            throw ExceptionFactory.create(RoomError.DB_ERROR);

        RoomDTO roomDTO = conversionService.convert(foundRoom, RoomDTO.class);
        roomDTO.setUsers(foundRoom.getUsers().stream().map(new Function<User, UserDTO>() {
            @Override
            public UserDTO apply(User user) {
               return conversionService.convert(user, UserDTO.class);
            }
        }).collect(Collectors.toList()));
        return roomDTO;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public void leaveRoom(UserToRoomDTO userToRoomDTO){
        User userDTO = CRUDUserRepository.findOne(userToRoomDTO.getUserId());

        if(userDTO == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);

        Room foundRoom = CRUDRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!foundRoom.removeUser(userDTO)){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }

        CRUDRoomRepository.save(foundRoom);
        foundRoom = CRUDRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null)
            throw ExceptionFactory.create(RoomError.DB_ERROR);
    }

    @Transactional
    public MessageDTO addMessage(MessageDTO messageDTO){
        Room room = CRUDRoomRepository.findOne(messageDTO.getRoomId());
        Message message = conversionService.convert(messageDTO, Message.class);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        room.addMessage(message);
        CRUDRoomRepository.saveAndFlush(room);
        return conversionService.convert(message, MessageDTO.class);
    }

    public List<Message> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = CRUDRoomRepository.findAllMessages(pageable, roomId);
        return page.getContent();
    }
}
