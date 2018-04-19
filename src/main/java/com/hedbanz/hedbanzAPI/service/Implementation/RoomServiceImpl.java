package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CRUDRoomRepository;
import com.hedbanz.hedbanzAPI.repository.CRUDUserRepository;
import com.hedbanz.hedbanzAPI.repository.RoomRepositoryFunctional;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.utils.ErrorUtil;
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
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService{
    @Qualifier("APIConversionService")
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private CRUDRoomRepository crudRoomRepository;

    @Autowired
    private RoomRepositoryFunctional roomRepositoryFunctional;

    @Autowired
    private CRUDUserRepository crudUserRepository;

    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDTO addRoom(RoomDTO roomDTO){
        Room room = conversionService.convert(roomDTO, Room.class);
        Iterator<UserDTO> userIterator = roomDTO.getUsers().iterator();
        UserDTO userDTO= userIterator.next();
        User user = crudUserRepository.findOne(userDTO.getId());

        if(room.getName() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if(user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        if(room.getPassword().equals(""))
            room.setPassword(null);

        room.addPlayer(conversionService.convert(user, Player.class));
        room.setCurrentPlayersNumber(0);
        room.setRoomAdmin(user.getId());
        crudRoomRepository.saveAndFlush(room);

        room.setCurrentPlayersNumber(1);
        return conversionService.convert(room, RoomDTO.class);
    }

    public void deleteRoom(long roomId){
        crudRoomRepository.delete(roomId);
    }

    public RoomDTO getRoom(long roomId){
        return conversionService.convert(crudRoomRepository.findOne(roomId), RoomDTO.class);
    }

    @Cacheable("rooms")
    public List<RoomDTO> getAllRooms(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<RoomDTO> page = crudRoomRepository.findAllRooms(pageable);

        return page.getContent();
    }

    public List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber){
        return roomRepositoryFunctional.findRoomsByFilter(roomFilterDTO,pageNumber,Constants.PAGE_SIZE);
    }

    public List<Message> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<Message> page = crudRoomRepository.findAllMessages(pageable, roomId);
        return page.getContent();
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public UserDTO leaveRoom(UserToRoomDTO userToRoomDTO) {
        UserDTO result = new UserDTO.UserDTOBuilder().createUserDTO();
        if(userToRoomDTO.getUserId() == null || userToRoomDTO.getRoomId() == null){
            result.setCustomError(ErrorUtil.getError(RoomError.INCORRECT_INPUT));
            return result;
        }
        User user = crudUserRepository.findOne(userToRoomDTO.getUserId());

        if(user == null) {
            result.setCustomError(ErrorUtil.getError(RoomError.WRONG_USER));
            return result;
        }

        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!foundRoom.removePlayer(conversionService.convert(user, Player.class))){
            result.setCustomError(ErrorUtil.getError(RoomError.NO_SUCH_USER_IN_ROOM));
            return result;
        }

        crudRoomRepository.save(foundRoom);
        foundRoom = crudRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null) {
            result.setCustomError(ErrorUtil.getError(RoomError.DB_ERROR));
            return result;
        }
        return result;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO) {
        RoomDTO result = new RoomDTO();
        if(userToRoomDTO.getUserId() == null || userToRoomDTO.getRoomId() == null){
            result.setCustomError(ErrorUtil.getError(RoomError.INCORRECT_INPUT));
            return result;
        }
        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());

        if(foundRoom.getUserCount() == foundRoom.getMaxPlayers()) {
            result.setCustomError(ErrorUtil.getError(RoomError.ROOM_FULL));
            return result;
        }

        if(!TextUtils.isEmpty(foundRoom.getPassword())&&!TextUtils.isEmpty(userToRoomDTO.getPassword()))
            if(!foundRoom.getPassword().equals(userToRoomDTO.getPassword())) {
                result.setCustomError(ErrorUtil.getError(RoomError.WRONG_PASSWORD));
                return result;
            }

        User user = crudUserRepository.findOne(userToRoomDTO.getUserId());
        if(user == null){
            result.setCustomError(ErrorUtil.getError(RoomError.WRONG_USER));
            return result;
        }

        if(!foundRoom.addPlayer(conversionService.convert(user, Player.class))){
            result.setCustomError(ErrorUtil.getError(RoomError.ALREADY_IN_ROOM));
            return result;
        }
        crudRoomRepository.saveAndFlush(foundRoom);
        foundRoom = crudRoomRepository.findOne(foundRoom.getId());

        if(foundRoom == null) {
            result.setCustomError(ErrorUtil.getError(RoomError.DB_ERROR));
            return result;
        }

        RoomDTO roomDTO = conversionService.convert(foundRoom, RoomDTO.class);
        roomDTO.setUsers(foundRoom.getPlayers().stream().map(userConv -> conversionService.convert(userConv, UserDTO.class)).collect(Collectors.toList()));
        return roomDTO;
    }

    public UserToRoomDTO checkRoomPassword(UserToRoomDTO userToRoomDTO) {
        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!TextUtils.isEmpty(foundRoom.getPassword())&&!TextUtils.isEmpty(userToRoomDTO.getPassword()))
            if(!foundRoom.getPassword().equals(userToRoomDTO.getPassword())) {
                userToRoomDTO.setCustomError(ErrorUtil.getError(RoomError.WRONG_PASSWORD));
                return userToRoomDTO;
            }
        return userToRoomDTO;
    }

    @Transactional
    public MessageDTO addMessage(MessageDTO messageDTO) {
        Room room = crudRoomRepository.findOne(messageDTO.getRoomId());
        Message message = conversionService.convert(messageDTO, Message.class);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        room.addMessage(message);
        crudRoomRepository.saveAndFlush(room);
        return conversionService.convert(message, MessageDTO.class);
    }

    public SetWordDTO setPlayerWord(SetWordDTO setWordDTO){
        if(setWordDTO.getSenderId() == null || setWordDTO.getWordReceiverId() == null || setWordDTO.getWordReceiverId() == null){
            setWordDTO.setError(ErrorUtil.getError(RoomError.INCORRECT_INPUT));
            return setWordDTO;
        }

        if(crudRoomRepository.setPlayerWord(setWordDTO.getWord(), setWordDTO.getWordReceiverId()) == 0){
            setWordDTO.setError(ErrorUtil.getError(RoomError.DB_ERROR));
            return setWordDTO;
        }

        return setWordDTO;
    }
}
