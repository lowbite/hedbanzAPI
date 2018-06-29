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
import com.hedbanz.hedbanzAPI.service.MessageService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.transfer.RoomFilter;
import com.hedbanz.hedbanzAPI.transfer.WordDto;
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

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private static final int MAX_GUESS_ATTEMPTS = 3;
    private static final int MAX_ACTIVE_ROOMS = 15;
    private final ConversionService conversionService;
    private final FcmServiceImpl fcmService;
    private final MessageService messageService;
    private final CrudRoomRepository crudRoomRepository;
    private final RoomRepositoryFunctional roomRepositoryFunctional;
    private final CrudUserRepository crudUserRepository;
    private final CrudPlayerRepository crudPlayerRepository;
    private final CrudMessageRepository crudMessageRepository;

    @Autowired
    public RoomServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                           FcmServiceImpl fcmService, MessageService messageService,
                           CrudRoomRepository crudRoomRepository, RoomRepositoryFunctional roomRepositoryFunctional,
                           CrudUserRepository crudUserRepository, CrudPlayerRepository crudPlayerRepository,
                           CrudMessageRepository crudMessageRepository) {
        this.conversionService = conversionService;
        this.fcmService = fcmService;
        this.messageService = messageService;
        this.crudRoomRepository = crudRoomRepository;
        this.roomRepositoryFunctional = roomRepositoryFunctional;
        this.crudUserRepository = crudUserRepository;
        this.crudPlayerRepository = crudPlayerRepository;
        this.crudMessageRepository = crudMessageRepository;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Room addRoom(Room room, Long creatorId) {
        if (creatorId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        User user = crudUserRepository.findOne(creatorId);

        if (room.getName() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if (user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        if (room.getPassword().equals(""))
            room.setPassword(null);

        Player player = conversionService.convert(user, Player.class);
        player.setStatus(PlayerStatus.ACTIVE);

        room.addPlayer(player);
        room.setCurrentPlayersNumber(1);
        room.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        room.setRoomAdmin(user.getId());
        room = crudRoomRepository.saveAndFlush(room);

        Message message = Message.Builder().setRoom(room)
                .setSenderUser(user)
                .setType(MessageType.JOINED_USER)
                .setQuestion(null)
                .build();
        crudMessageRepository.saveAndFlush(message);
        return room;
    }

    public void deleteRoom(Long roomId) {
        crudRoomRepository.delete(roomId);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Room getRoom(Long roomId) {
        Room room = crudRoomRepository.findOne(roomId);
        if (room == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        return room;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public void checkPlayerInRoom(Long userId, Long roomId) {
        if (userId == null || roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
    }

    @Transactional
    public Room setPlayersWordSetters(Long roomId) {
        if (roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = crudRoomRepository.findOne(roomId);
        if (room == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }

        List<Player> players = room.getPlayers();
        Player player;
        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
            if (i + 1 < players.size()) {
                player.setWordSettingUserId(players.get(i + 1).getUser().getId());
            } else {
                player.setWordSettingUserId(players.get(0).getUser().getId());
            }
            messageService.addSettingWordMessage(player.getRoom().getId(), player.getUser().getId());
        }
        return crudRoomRepository.saveAndFlush(room);
    }

    @Cacheable("rooms")
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> page = crudRoomRepository.findAllRooms(pageable);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(Long userId) {/*
        List<Player> players = crudPlayerRepository.findPlayersByUserId(userId);
        return players.stream().map(Player::getRoom).collect(Collectors.toList());*/
        return crudRoomRepository.findActiveRooms(userId);
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByFilter(RoomFilter roomFilter, Integer pageNumber) {
        //TODO change roomFilter
        return roomRepositoryFunctional.findRoomsByFilter(roomFilter, pageNumber, Constants.PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRoomsByFilter(RoomFilter roomFilter, Long userId) {
        if (userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);

        return roomRepositoryFunctional.findActiveRoomsByFilter(roomFilter, userId);
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void leaveFromRoom(Long userId, Long roomId) {
        if (userId == null || roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        User user = crudUserRepository.findOne(userId);
        if (user == null) {
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }

        Room foundRoom = crudRoomRepository.findOne(roomId);
        Player player = foundRoom.getPlayerByLogin(user.getLogin());
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

        if (foundRoom.getGameStatus() == GameStatus.GUESSING_WORDS) {
            player.setStatus(PlayerStatus.LEFT);
            crudPlayerRepository.saveAndFlush(player);
        } else {
            foundRoom.removePlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = crudRoomRepository.saveAndFlush(foundRoom);
        }


        if (foundRoom.getCurrentPlayersNumber() == 0 || playersAbsent(foundRoom))
            crudRoomRepository.delete(foundRoom.getId());
        else {
            Message message = Message.Builder().setRoom(foundRoom)
                    .setSenderUser(user)
                    .setType(MessageType.LEFT_USER)
                    .setQuestion(null)
                    .build();
            crudMessageRepository.saveAndFlush(message);
        }
    }

    private boolean playersAbsent(Room room) {
        for (Player player : room.getPlayers()) {
            if (player.getStatus() != PlayerStatus.LEFT) {
                return false;
            }
        }
        return true;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 5)
    public Room addUserToRoom(Long userId, Long roomId, String password) {
        if (userId == null || roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        List<Room> activeRooms = crudRoomRepository.findActiveRooms(userId);
        if (activeRooms.size() >= MAX_ACTIVE_ROOMS)
            throw ExceptionFactory.create(RoomError.PLAYER_HAVE_ACTIVE_ROOMS_MAX_NUMBER);

        Room foundRoom = crudRoomRepository.findOne(roomId);
        if (foundRoom.getGameStatus() == GameStatus.SETTING_WORDS)
            throw ExceptionFactory.create(RoomError.GAME_HAS_BEEN_ALREADY_STARTED);

        User user = crudUserRepository.findOne(userId);
        if (user == null) {
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        }

        if (foundRoom.getGameStatus() != GameStatus.GUESSING_WORDS) {
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
        } else {
            Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
            if (player == null)
                throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

            player.setStatus(PlayerStatus.ACTIVE);
            crudPlayerRepository.saveAndFlush(player);
        }

        Message message = Message.Builder().setRoom(foundRoom)
                .setSenderUser(user)
                .setType(MessageType.JOINED_USER)
                .setQuestion(null)
                .build();
        crudMessageRepository.saveAndFlush(message);
        return foundRoom;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status) {
        if (userId == null || roomId == null || status == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = crudPlayerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
        if (player.getStatus().equals(status)) {
            return player;
        }
        player.setStatus(status);
        return crudPlayerRepository.saveAndFlush(player);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public void checkRoomPassword(Long roomId, String password) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room foundRoom = crudRoomRepository.findOne(roomId);
        if (!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(password) || !foundRoom.getPassword().equals(password))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean startGame(Long roomId) {
        Room room = crudRoomRepository.getOne(roomId);
        if (room.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS)
            return false;
        room.setGameStatus(GameStatus.SETTING_WORDS);
        crudRoomRepository.saveAndFlush(room);
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setPlayerWord(WordDto wordDto) {
        //TODO change input parameter
        if (wordDto.getSenderId() == null || wordDto.getWordReceiverId() == null
                || wordDto.getWord() == null || wordDto.getRoomId() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player wordReceiverPlayer = null;
        List<Player> players = crudPlayerRepository.findPlayersByRoomId(wordDto.getRoomId());
        for (Player player : players) {
            if (player.getUser().getId().equals(wordDto.getWordReceiverId())) {
                player.setWord(wordDto.getWord());
                wordReceiverPlayer = player;
                break;
            }
        }

        if (wordReceiverPlayer == null) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUser().getId().equals(wordDto.getSenderId())) {
                    if (i + 1 < players.size()) {
                        if (players.get(i + 1).getWord() == null) {
                            players.get(i + 1).setWord(wordDto.getWord());
                            wordReceiverPlayer = players.get(i + 1);
                        } else {
                            if (players.get(0).getWord() == null) {
                                players.get(0).setWord(wordDto.getWord());
                                wordReceiverPlayer = players.get(0);
                            }
                        }
                    }
                }
            }
        }
        if (wordReceiverPlayer != null)
            crudPlayerRepository.saveAndFlush(wordReceiverPlayer);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player startGuessing(Long roomId) {
        List<Player> players = crudPlayerRepository.findPlayersByRoomId(roomId);
        if (players == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player player = crudPlayerRepository.findOne(players.get(0).getId());
        player.setAttempt(1);
        Room room = crudRoomRepository.findOne(roomId);
        room.setGameStatus(GameStatus.GUESSING_WORDS);
        crudRoomRepository.saveAndFlush(room);
        crudPlayerRepository.saveAndFlush(player);
        return players.get(0);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Player nextGuessing(Long roomId) {
        List<Player> players = crudPlayerRepository.findPlayersByRoomId(roomId);
        if (players == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player resultPlayer = null;
        int attempt;
        int i = 0;
        while (i < players.size()) {
            attempt = players.get(i).getAttempt();
            if (attempt != 0 && players.get(i).getStatus() == PlayerStatus.ACTIVE) {
                if (attempt < MAX_GUESS_ATTEMPTS) {
                    resultPlayer = players.get(i);
                    resultPlayer.setAttempt(resultPlayer.getAttempt() + 1);
                    break;
                } else {
                    resultPlayer = getNextGuessingPlayer(players, i);
                    break;
                }
            } else if (players.get(i).getIsWinner()) {
                resultPlayer = getNextGuessingPlayer(players, i);
            }
            i++;
        }

        if (resultPlayer == null) {
            resultPlayer = players.get(0);
            resultPlayer.setAttempt(1);
        }

        crudPlayerRepository.updatePlayerAttempts(resultPlayer.getAttempt(), resultPlayer.getId());
        return resultPlayer;
    }

    private Player getNextGuessingPlayer(List<Player> players, int i) {
        Player resultPlayer;
        resultPlayer = players.get(i);
        crudPlayerRepository.updatePlayerAttempts(0, resultPlayer.getId());
        if (i + 1 < players.size()) {
            resultPlayer = players.get(i + 1);
            resultPlayer.setAttempt(1);
            return resultPlayer;
        } else {
            resultPlayer = players.get(0);
            resultPlayer.setAttempt(1);
            return resultPlayer;
        }
    }

    @Transactional(readOnly = true)
    public boolean isGameOver(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);

        Room room = crudRoomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        for (Player player : room.getPlayers()) {
            if (!player.getIsWinner()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public Room restartGame(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);

        Room room = crudRoomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        room.setGameStatus(GameStatus.SETTING_WORDS);

        for (Player player : room.getPlayers()) {
            if (player.getStatus() == PlayerStatus.LEFT)
                room.removePlayer(player);
            else {
                player.setIsWinner(false);
                player.setAttempt(0);
                player.setWord(null);
                player.setWordSettingUserId(null);
            }
        }
        return crudRoomRepository.saveAndFlush(room);
    }
}
