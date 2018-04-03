package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.entity.*;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.RoomException;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        Iterator<User> userIterator = room.getUsers().iterator();
        User userDTO = userIterator.next();
        Set<User> userDTOS = new HashSet<>();
        userDTO = CRUDUserRepository.findOne(userDTO.getId());

        if(room.getName() == null)
            throw new RoomException(new CustomError(RoomError.INCORRECT_INPUT.getErrorCode(), RoomError.INCORRECT_INPUT.getErrorMessage()));
        if(userDTO == null)
            throw new RoomException(new CustomError(RoomError.WRONG_USER.getErrorCode(), RoomError.WRONG_USER.getErrorMessage()));
        if(room.getPassword().equals(""))
            room.setPassword(null);

        userDTOS.add(userDTO);
        room.setUsers(userDTOS);
        Room newRoom = CRUDRoomRepository.saveAndFlush(room);

        if(newRoom == null)
            throw new RoomException(new CustomError(RoomError.DB_ERROR.getErrorCode(), RoomError.DB_ERROR.getErrorMessage()));

        newRoom.setCurrentPlayersNumber(1);
        return conversionService.convert(newRoom, RoomDTO.class);
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
            throw new RoomException(new CustomError(RoomError.ROOM_FULL.getErrorCode(), RoomError.ROOM_FULL.getErrorMessage()));

        if(!TextUtils.isEmpty(foundRoom.getPassword())&&!TextUtils.isEmpty(userToRoomDTO.getPassword()))
            if(!foundRoom.getPassword().equals(userToRoomDTO.getPassword()))
                throw new RoomException(new CustomError(
                        RoomError.WRONG_PASSWORD.getErrorCode(),
                        RoomError.WRONG_PASSWORD.getErrorMessage()));

        User userDTO = CRUDUserRepository.findOne(userToRoomDTO.getUserId());
        if(userDTO == null)
            throw new RoomException(new CustomError(
                    RoomError.WRONG_USER.getErrorCode(),
                    RoomError.WRONG_USER.getErrorMessage()));

        if(!foundRoom.addUser(userDTO)){
            throw new RoomException(new CustomError(
                    RoomError.ALREADY_IN_ROOM.getErrorCode(),
                    RoomError.ALREADY_IN_ROOM.getErrorMessage()));
        }
        CRUDRoomRepository.saveAndFlush(foundRoom);
        foundRoom = CRUDRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null)
            throw new RoomException(new CustomError(
                    RoomError.DB_ERROR.getErrorCode(),
                    RoomError.DB_ERROR.getErrorMessage()));

        return conversionService.convert(foundRoom, RoomDTO.class);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public void leaveRoom(UserToRoomDTO userToRoomDTO){
        User userDTO = CRUDUserRepository.findOne(userToRoomDTO.getUserId());

        if(userDTO == null)
            throw new RoomException(new CustomError(
                    RoomError.WRONG_USER.getErrorCode(),
                    RoomError.WRONG_USER.getErrorMessage()));

        Room foundRoom = CRUDRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!foundRoom.removeUser(userDTO)){
            throw new RoomException(new CustomError(
                    RoomError.NO_SUCH_USER_IN_ROOM.getErrorCode(),
                    RoomError.NO_SUCH_USER_IN_ROOM.getErrorMessage()));
        }

        CRUDRoomRepository.save(foundRoom);
        foundRoom = CRUDRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null)
            throw new RoomException(new CustomError(
                    RoomError.DB_ERROR.getErrorCode(),
                    RoomError.DB_ERROR.getErrorMessage()));
    }

    @Transactional
    public void addMessage(MessageDTO messageDTO){
        Room room = CRUDRoomRepository.findOne(messageDTO.getRoomId());
        Message message = conversionService.convert(messageDTO, Message.class);
        room.addMessage(message);
        CRUDRoomRepository.saveAndFlush(room);
    }

    public List<Message> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = CRUDRoomRepository.findAllMessages(pageable, roomId);
        return page.getContent();
    }
}
