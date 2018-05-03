package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.DTO.*;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.entity.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService{
    private final ConversionService conversionService;
    private final UserService userService;
    private final MessageService messageService;
    private final CrudRoomRepository crudRoomRepository;
    private final RoomRepositoryFunctional roomRepositoryFunctional;
    private final CrudUserRepository crudUserRepository;
    private final CrudPlayerRepository crudPlayerRepository;
    private final CrudMessageRepository crudMessageRepository;

    @Autowired
    public RoomServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                           UserService userService, MessageService messageService, CrudRoomRepository crudRoomRepository,
                           RoomRepositoryFunctional roomRepositoryFunctional, CrudUserRepository crudUserRepository,
                           CrudPlayerRepository crudPlayerRepository, CrudMessageRepository crudMessageRepository) {
        this.conversionService = conversionService;
        this.userService = userService;
        this.crudRoomRepository = crudRoomRepository;
        this.roomRepositoryFunctional = roomRepositoryFunctional;
        this.crudUserRepository = crudUserRepository;
        this.messageService = messageService;
        this.crudPlayerRepository = crudPlayerRepository;
        this.crudMessageRepository = crudMessageRepository;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public RoomDTO addRoom(RoomDTO roomDTO){
        Room room = conversionService.convert(roomDTO, Room.class);
        Iterator<PlayerDTO> userIterator = roomDTO.getPlayers().iterator();
        PlayerDTO playerDTO= userIterator.next();
        User user = crudUserRepository.findOne(playerDTO.getId());

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
        message.setQuestion(null);

        crudMessageRepository.saveAndFlush(message);
        crudRoomRepository.saveAndFlush(room);

        room.setCurrentPlayersNumber(1);
        return conversionService.convert(room, RoomDTO.class);
    }

    public void deleteRoom(long roomId){
        crudRoomRepository.delete(roomId);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public RoomDTO getRoom(long roomId){
        Room foundRoom = crudRoomRepository.findOne(roomId);
        RoomDTO roomDTO = conversionService.convert(foundRoom, RoomDTO.class);
        roomDTO.setPlayers(foundRoom.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDTO.class)).collect(Collectors.toList()));
        return roomDTO;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public void checkPlayerInRoom(UserToRoomDTO userToRoomDTO){
        if(userToRoomDTO.getUserId() == null || userToRoomDTO.getRoomId() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(room == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        User user = crudUserRepository.findOne(userToRoomDTO.getUserId());
        if(user == null){
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);
        }

        if(!room.isContainPlayer(conversionService.convert(user, Player.class))){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
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

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void leaveRoom(UserToRoomDTO userToRoomDTO) {
        if(userToRoomDTO.getUserId() == null || userToRoomDTO.getRoomId() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        User user = crudUserRepository.findOne(userToRoomDTO.getUserId());
        if(user == null) {
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }
        Player player = crudPlayerRepository.findOne(user.getId());
        List<PlayerDTO> players = crudRoomRepository.findPlayers(userToRoomDTO.getRoomId());
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getId().equals(user.getId())){
                if(i + 1 < players.size()) {
                    if (players.get(i + 1).getWord() == null) {
                        players.get(i + 1).setWord(player.getWord());
                    }
                }else{
                    if (players.get(0).getWord() == null) {
                        players.get(0).setWord(player.getWord());
                    }
                }
            }
        }

        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!foundRoom.removePlayer(conversionService.convert(user, Player.class))){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }

        Message message = new Message();
        message.setRoomId(foundRoom.getId());
        message.setSenderUser(user);
        message.setType(MessageType.LEFT_USER.getCode());
        message.setQuestion(null);
        crudMessageRepository.saveAndFlush(message);
        crudRoomRepository.saveAndFlush(foundRoom);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, timeout = 5)
    public RoomDTO addUserToRoom(UserToRoomDTO userToRoomDTO) {
        //TODO add checking for game start
        if(userToRoomDTO.getUserId() == null || userToRoomDTO.getRoomId() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());

        if(foundRoom.getUserCount() == foundRoom.getMaxPlayers()) {
            throw ExceptionFactory.create(RoomError.ROOM_FULL);
        }
        if(!TextUtils.isEmpty(foundRoom.getPassword())&&!TextUtils.isEmpty(userToRoomDTO.getPassword()))
            if(!foundRoom.getPassword().equals(userToRoomDTO.getPassword())) {
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
            }

        User user = crudUserRepository.findOne(userToRoomDTO.getUserId());
        if(user == null){
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }

        if(!foundRoom.addPlayer(conversionService.convert(user, Player.class))){
            throw ExceptionFactory.create(RoomError.ALREADY_IN_ROOM);
        }

        Message message = new Message();
        message.setRoomId(foundRoom.getId());
        message.setSenderUser(user);
        message.setType(MessageType.JOINED_USER.getCode());
        message.setQuestion(null);
        crudMessageRepository.saveAndFlush(message);
        foundRoom = crudRoomRepository.saveAndFlush(foundRoom);
        RoomDTO resultRoom = conversionService.convert(foundRoom, RoomDTO.class);
        resultRoom.setPlayers(foundRoom.getPlayers().stream().map(player -> conversionService.convert(player, PlayerDTO.class)).collect(Collectors.toList()));
        List<FriendDTO> friends = crudUserRepository.getAcceptedFriends(user.getId());
        for (FriendDTO friend : friends){
            for(PlayerDTO player : resultRoom.getPlayers()) {
                if (friend.getId().equals(player.getId())) {
                    player.setIsFriend(true);
                }
            }
        }
        return resultRoom;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PlayerDTO setPlayerAFK(UserToRoomDTO userToRoom, Boolean isAFK) {
        if(userToRoom.getUserId() == null || userToRoom.getRoomId() == null || isAFK == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = crudRoomRepository.findOne(userToRoom.getRoomId());
        Player player = crudPlayerRepository.findOne(userToRoom.getUserId());
        if(player == null || !room.isContainPlayer(player)){
            return null;
        }
        player.setIsAFK(isAFK);
        player = crudPlayerRepository.saveAndFlush(player);
        return conversionService.convert(player, PlayerDTO.class);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public void checkRoomPassword(UserToRoomDTO userToRoomDTO) {
        if(userToRoomDTO.getRoomId() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room foundRoom = crudRoomRepository.findOne(userToRoomDTO.getRoomId());
        if(!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(userToRoomDTO.getPassword()) || !foundRoom.getPassword().equals(userToRoomDTO.getPassword()))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<PlayerDTO> getPlayers(long roomId) {
        return crudRoomRepository.findPlayers(roomId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Boolean startGame(long roomId) {
        Room room = crudRoomRepository.getOne(roomId);
        if(room.getIsGameStarted())
            return true;//TODO change to false
        room.setIsGameStarted(true);
        crudRoomRepository.saveAndFlush(room);
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setPlayerWord(WordDTO wordDTO){
        if(wordDTO.getSenderId() == null || wordDTO.getWordReceiverId() == null || wordDTO.getWord() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player playerResult = null;
        Room room = crudRoomRepository.findOne(wordDTO.getRoomId());
        for(Player player: room.getPlayers()) {
            if(player.getId().equals(wordDTO.getWordReceiverId())) {
                player.setWord(wordDTO.getWord());
                playerResult = player;
                break;
            }
        }

        PlayerDTO playerDTOResult = null;
        if(playerResult == null) {
            List<PlayerDTO> players = crudRoomRepository.findPlayers(room.getId());
            for (int i = 0; i < players.size(); i++) {
                if(players.get(i).getId().equals(wordDTO.getSenderId())){
                    if(i + 1 != players.size()) {
                        if (players.get(i + 1).getWord() == null) {
                            players.get(i + 1).setWord(wordDTO.getWord());
                            playerDTOResult = players.get(i + 1);
                        }
                    }else{
                        if (players.get(0).getWord() == null) {
                            players.get(0).setWord(wordDTO.getWord());
                            playerDTOResult = players.get(0);
                        }
                    }
                }
            }
        }
        if(playerDTOResult != null)
            crudPlayerRepository.saveAndFlush(conversionService.convert(playerDTOResult, Player.class));
        crudRoomRepository.saveAndFlush(room);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PlayerDTO startGuessing(long roomId){
        List<PlayerDTO> players = crudRoomRepository.findPlayers(roomId);
        if(players == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player player = crudPlayerRepository.findOne(players.get(0).getId());
        player.setAttempts(1);
        crudPlayerRepository.saveAndFlush(player);
        return players.get(0);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PlayerDTO nextGuessing(long roomId){
        List<PlayerDTO> players = crudRoomRepository.findPlayers(roomId);
        if(players == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        PlayerDTO resultPlayer = null;
        Integer attempts;
        for (int i = 0; i < players.size(); i++) {
            attempts = players.get(i).getAttempts();
            if(attempts != null){
                if(attempts < 3){
                    resultPlayer = players.get(i);
                    resultPlayer.setAttempts(resultPlayer.getAttempts() + 1);
                    break;
                }else{
                    if(i + 1 < players.size()){
                        resultPlayer = players.get(i);
                        resultPlayer.setAttempts(1);
                        break;
                    }else{
                        resultPlayer = players.get(0);
                        resultPlayer.setAttempts(1);
                        break;
                    }
                }
            }
        }

        crudPlayerRepository.saveAndFlush(conversionService.convert(resultPlayer, Player.class));
        return resultPlayer;
    }
}
