package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.transfer.*;
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
    private final CrudRoomRepository crudRoomRepository;
    private final RoomRepositoryFunctional roomRepositoryFunctional;
    private final CrudUserRepository crudUserRepository;
    private final CrudPlayerRepository crudPlayerRepository;
    private final CrudMessageRepository crudMessageRepository;

    @Autowired
    public RoomServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                           CrudRoomRepository crudRoomRepository, RoomRepositoryFunctional roomRepositoryFunctional,
                           CrudUserRepository crudUserRepository, CrudPlayerRepository crudPlayerRepository,
                           CrudMessageRepository crudMessageRepository) {
        this.conversionService = conversionService;
        this.crudRoomRepository = crudRoomRepository;
        this.roomRepositoryFunctional = roomRepositoryFunctional;
        this.crudUserRepository = crudUserRepository;
        this.crudPlayerRepository = crudPlayerRepository;
        this.crudMessageRepository = crudMessageRepository;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Room addRoom(Room room, Long creatorId){
        if(creatorId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        User user = crudUserRepository.findOne(creatorId);

        if(room.getName() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if(user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        if(room.getPassword().equals(""))
            room.setPassword(null);

        Player player = conversionService.convert(user, Player.class);
        player.setStatus(PlayerStatus.ACTIVE);

        room.addPlayer(player);
        room.setCurrentPlayersNumber(1);
        room.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        room.setRoomAdmin(user.getId());
        room = crudRoomRepository.saveAndFlush(room);

        Message message = Message.MessageBuilder().setRoom(room)
                                                .setSenderUser(user)
                                                .setType(MessageType.JOINED_USER)
                                                .setQuestion(null)
                                                .build();
        crudMessageRepository.saveAndFlush(message);
        return room;
    }

    public void deleteRoom(Long roomId){
        crudRoomRepository.delete(roomId);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Room getRoom(Long roomId){
        return crudRoomRepository.findOne(roomId);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public void checkPlayerInRoom(Long userId, Long roomId){
        if(userId == null || roomId == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = crudRoomRepository.findOne(roomId);
        if(room == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        User user = crudUserRepository.findOne(userId);
        if(user == null){
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);
        }

        if(!room.containsPlayer(conversionService.convert(user, Player.class))){
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
    }

    @Cacheable("rooms")
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> page = crudRoomRepository.findAllRooms(pageable);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(Long userId){
        List<Player> players = crudPlayerRepository.findPlayersByUserId(userId);
        return players.stream().map(Player::getRoom).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByFilter(RoomFilterDto roomFilterDto, Integer pageNumber){
        //TODO change roomFilterDto
        return roomRepositoryFunctional.findRoomsByFilter(roomFilterDto,pageNumber,Constants.PAGE_SIZE);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void leaveFromRoom(Long userId, Long roomId) {
        if(userId == null || roomId == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        User user = crudUserRepository.findOne(userId);
        if(user == null) {
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }

        Room foundRoom = crudRoomRepository.findOne(roomId);
        Player player = foundRoom.getPlayerByLogin(user.getLogin());
        if(player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        List<Player> players = crudRoomRepository.findPlayers(roomId);
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

        if(foundRoom.getGameStatus() == GameStatus.GUESSING_WORDS) {
            player.setStatus(PlayerStatus.LEFT);
            crudPlayerRepository.saveAndFlush(player);
        }else{
            foundRoom.removePlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
        }

        Message message = Message.MessageBuilder().setRoom(foundRoom)
                                                    .setSenderUser(user)
                                                    .setType(MessageType.LEFT_USER)
                                                    .setQuestion(null)
                                                    .build();
        crudMessageRepository.saveAndFlush(message);
        crudRoomRepository.saveAndFlush(foundRoom);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 5)
    public Room addUserToRoom(Long userId, Long roomId, String password) {
        //TODO add checking for game start
        if(userId == null || roomId == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Room foundRoom = crudRoomRepository.findOne(roomId);
        User user = crudUserRepository.findOne(userId);
        if(user == null){
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }

        if(foundRoom.getGameStatus() != GameStatus.GUESSING_WORDS) {
            if (foundRoom.getUserCount() == foundRoom.getMaxPlayers()) {
                throw ExceptionFactory.create(RoomError.ROOM_FULL);
            }
            if (!TextUtils.isEmpty(foundRoom.getPassword()) && !TextUtils.isEmpty(password))
                if (!foundRoom.getPassword().equals(password)) {
                    throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
                }
            Player player = conversionService.convert(user, Player.class);
            player.setStatus(PlayerStatus.ACTIVE);
            foundRoom.addPlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = crudRoomRepository.saveAndFlush(foundRoom);
        }else{
            Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
            if(player == null)
                throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

            player.setStatus(PlayerStatus.ACTIVE);
            crudPlayerRepository.saveAndFlush(player);
        }

        Message message = Message.MessageBuilder().setRoom(foundRoom)
                                                .setSenderUser(user)
                                                .setType(MessageType.JOINED_USER)
                                                .setQuestion(null)
                                                .build();
        crudMessageRepository.saveAndFlush(message);
        return foundRoom;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status) {
        if(userId == null || roomId == null || status == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if(player == null || !player.getRoom().getId().equals(roomId)){
            return null;
        }
        player.setStatus(status);
        return crudPlayerRepository.saveAndFlush(player);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public void checkRoomPassword(Long roomId, String password) {
        if(roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room foundRoom = crudRoomRepository.findOne(roomId);
        if(!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(password) || !foundRoom.getPassword().equals(password))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Player> getPlayers(Long roomId) {
        return crudRoomRepository.findPlayers(roomId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean startGame(Long roomId) {
        Room room = crudRoomRepository.getOne(roomId);
        if(room.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS)
            return true;//TODO change to false
        room.setGameStatus(GameStatus.SETTING_WORDS);
        crudRoomRepository.saveAndFlush(room);
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setPlayerWord(WordDto wordDto){
        //TODO change input parameter
        if(wordDto.getSenderId() == null || wordDto.getWordReceiverId() == null || wordDto.getWord() == null){
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player wordReceiverPlayer = null;
        Room room = crudRoomRepository.findOne(wordDto.getRoomId());
        Set<Player> players = room.getPlayers();
        for(Player player: players) {
            if(player.getUser().getId().equals(wordDto.getWordReceiverId())) {
                player.setWord(wordDto.getWord());
                wordReceiverPlayer = player;
                break;
            }
        }

        if(wordReceiverPlayer == null) {
            Iterator<Player> iterator = players.iterator();
            while (iterator.hasNext()){
                if(iterator.next().getUser().getId().equals(wordDto.getSenderId())){
                    if(!iterator.hasNext())
                        iterator = players.iterator();

                    wordReceiverPlayer = iterator.next();
                    wordReceiverPlayer.setWord(wordDto.getWord());
                }
            }

           /* List<PlayerDto> players = crudRoomRepository.findPlayers(room.getId());
            for (int i = 0; i < players.size(); i++) {
                if(players.get(i).getId().equals(wordDto.getSenderId())){
                    if(i + 1 != players.size()) {
                        if (players.get(i + 1).getWord() == null) {
                            players.get(i + 1).setWord(wordDto.getWord());
                            playerDtoResult = players.get(i + 1);
                        }
                    }else{
                        if (players.get(0).getWord() == null) {
                            players.get(0).setWord(wordDto.getWord());
                            playerDtoResult = players.get(0);
                        }
                    }
                }
            }*/
        }

        if(wordReceiverPlayer != null)
            crudPlayerRepository.saveAndFlush(wordReceiverPlayer);
        crudRoomRepository.saveAndFlush(room);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player startGuessing(Long roomId){
        List<Player> players = crudRoomRepository.findPlayers(roomId);
        if(players == null){
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player player = crudPlayerRepository.findOne(players.get(0).getId());
        player.setAttempts(1);
        Room room = crudRoomRepository.findOne(roomId);
        room.setGameStatus(GameStatus.GUESSING_WORDS);
        crudRoomRepository.saveAndFlush(room);
        crudPlayerRepository.saveAndFlush(player);
        return players.get(0);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player nextGuessing(Long roomId){
        List<Player> players = crudRoomRepository.findPlayers(roomId);
        if(players == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player resultPlayer = null;
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
        crudPlayerRepository.updatePlayerAttempts(resultPlayer.getAttempts(), resultPlayer.getId());
        return resultPlayer;
    }
}
