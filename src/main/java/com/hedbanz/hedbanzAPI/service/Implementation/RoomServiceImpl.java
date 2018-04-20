package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService{
    private final ConversionService conversionService;

    private final CRUDRoomRepository crudRoomRepository;

    private final RoomRepositoryFunctional roomRepositoryFunctional;

    private final CRUDUserRepository crudUserRepository;

    @Autowired
    public RoomServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                           CRUDRoomRepository crudRoomRepository, RoomRepositoryFunctional roomRepositoryFunctional,
                           CRUDUserRepository crudUserRepository) {
        this.conversionService = conversionService;
        this.crudRoomRepository = crudRoomRepository;
        this.roomRepositoryFunctional = roomRepositoryFunctional;
        this.crudUserRepository = crudUserRepository;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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

        Message message = new Message();
        message.setRoomId(room.getId());
        message.setSenderUser(user);
        message.setType(MessageType.JOINED_USER.getCode());
        room.addMessage(message);

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
    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<RoomDTO> page = crudRoomRepository.findAllRooms(pageable);

        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByFilter(RoomFilterDTO roomFilterDTO, int pageNumber){
        return roomRepositoryFunctional.findRoomsByFilter(roomFilterDTO,pageNumber,Constants.PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getAllMessages(long roomId, int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE);
        Page<MessageDTO> page = crudRoomRepository.findAllMessages(pageable, roomId);
        ArrayList<MessageDTO> messages = new ArrayList<>(page.getContent());
        Collections.reverse(messages);
        return messages;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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


        Message message = new Message();
        message.setRoomId(foundRoom.getId());
        message.setSenderUser(user);
        message.setType(MessageType.LEFT_USER.getCode());
        foundRoom.addMessage(message);

        crudRoomRepository.saveAndFlush(foundRoom);
        foundRoom.setCurrentPlayersNumber(foundRoom.getCurrentPlayersNumber() - 1);
        return result;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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

        Message message = new Message();
        message.setRoomId(foundRoom.getId());
        message.setSenderUser(user);
        message.setType(MessageType.JOINED_USER.getCode());
        foundRoom.addMessage(message);

        crudRoomRepository.saveAndFlush(foundRoom);
        foundRoom.setCurrentPlayersNumber(foundRoom.getCurrentPlayersNumber() + 1);

        RoomDTO roomDTO = conversionService.convert(foundRoom, RoomDTO.class);
        roomDTO.setUsers(foundRoom.getPlayers().stream().map(userConv -> conversionService.convert(userConv, UserDTO.class)).collect(Collectors.toList()));
        return roomDTO;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Player> getPlayers(long roomId) {
        return crudRoomRepository.findPlayers(roomId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void checkRoomPassword(UserToRoomDTO userToRoomDTO) {
        if(userToRoomDTO.getRoomId() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(userToRoomDTO.getPassword()))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
            else if (!foundRoom.getPassword().equals(userToRoomDTO.getPassword())) {
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
            }
        }
    }

    @Transactional
    public MessageDTO addMessage(MessageDTO messageDTO) {
        if(messageDTO.getRoomId() == null || messageDTO.getClientMessageId() == null || messageDTO.getText() == null ||
                messageDTO.getType() == null || messageDTO.getSenderUser() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Room room = crudRoomRepository.findOne(messageDTO.getRoomId());
        User sender = crudUserRepository.findOne(messageDTO.getSenderUser().getId());
        Message message = conversionService.convert(messageDTO, Message.class);
        message.setSenderUser(sender);
        message.setCreateDate(new Timestamp(new Date().getTime()));
        room.addMessage(message);
        crudRoomRepository.saveAndFlush(room);
        return conversionService.convert(message, MessageDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WordDTO setPlayerWord(WordDTO wordDTO){
        if(wordDTO.getSenderId() == null || wordDTO.getWordReceiverId() == null || wordDTO.getWordReceiverId() == null){
            wordDTO.setError(ErrorUtil.getError(RoomError.INCORRECT_INPUT));
            return wordDTO;
        }

        Room room = crudRoomRepository.findOne(wordDTO.getRoomId());
        for (Player player: room.getPlayers()) {
            if(player.getId() == wordDTO.getWordReceiverId()) {
                player.setWord(wordDTO.getWord());
                break;
            }
        }
        crudRoomRepository.saveAndFlush(room);
        return wordDTO;
    }
}
