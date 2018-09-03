package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.RoomFilterSpecification;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.model.RoomFilter;
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
    private static final int MAX_ACTIVE_ROOMS = 15;
    private final ConversionService conversionService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public RoomServiceImpl(@Qualifier("APIConversionService") ConversionService conversionService,
                           RoomRepository roomRepository, UserRepository userRepository,
                           PlayerRepository playerRepository, MessageRepository messageRepository) {
        this.conversionService = conversionService;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.messageRepository = messageRepository;
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Room addRoom(Room room, Long creatorId) {
        if (creatorId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (room.getName() == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_NAME);
        if (room.getIconId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_ICON_ID);
        if (room.getStickerId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_STICKER_ID);

        Room repositoryRoomByName = roomRepository.findRoomByName(room.getName());
        if (repositoryRoomByName != null)
            throw ExceptionFactory.create(RoomError.ROOM_WITH_SUCH_NAME_ALREADY_EXIST);

        User user = userRepository.findOne(creatorId);
        if (user == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);
        List<Room> activeRooms = roomRepository.findActiveRooms(creatorId);
        if (activeRooms.size() >= MAX_ACTIVE_ROOMS)
            throw ExceptionFactory.create(RoomError.MAX_ACTIVE_ROOMS_NUMBER);
        if (room.getPassword().equals("")) {
            room.setPassword(null);
            room.setIsPrivate(false);
        } else
            room.setIsPrivate(true);

        Player player = conversionService.convert(user, Player.class);
        player.setStatus(PlayerStatus.ACTIVE);
        player.setAttempt(0);

        room.addPlayer(player);
        room.setCurrentPlayersNumber(1);
        room.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        room.setRoomAdmin(user.getUserId());
        room = roomRepository.saveAndFlush(room);

        Message message = Message.Builder().setRoom(room)
                .setSenderUser(user)
                .setType(MessageType.JOINED_USER)
                .setQuestion(null)
                .build();
        messageRepository.saveAndFlush(message);
        return room;
    }

    public void deleteRoom(Long roomId) {
        roomRepository.delete(roomId);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Room getRoom(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        Room room = roomRepository.findOne(roomId);
        if (room == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);
        }
        return room;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public void checkPlayerInRoom(Long userId, Long roomId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
        }
    }

    @Cacheable("rooms")
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> page = roomRepository.findAllRooms(pageable);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(Long userId) {
        if(userId == null){
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        return roomRepository.findActiveRooms(userId);
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByFilter(RoomFilter roomFilter, Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> rooms = roomRepository.findAll(new RoomFilterSpecification(roomFilter), pageable);
        return rooms.getContent();
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRoomsByFilter(RoomFilter roomFilter, Long userId) {
        if (userId == null)
            throw ExceptionFactory.create(InputError.INCORRECT_USER_ID);

        return roomRepository.findAll(new RoomFilterSpecification(roomFilter, userId));
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Room leaveUserFromRoom(Long userId, Long roomId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }

        User user = userRepository.findOne(userId);
        if (user == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);

        Room foundRoom = roomRepository.findOne(roomId);
        Player player = foundRoom.getPlayerByLogin(user.getLogin());
        if (player == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);

        if (foundRoom.getGameStatus() == GameStatus.GUESSING_WORDS || foundRoom.getGameStatus() == GameStatus.GAME_OVER) {
            player.setStatus(PlayerStatus.LEFT);
            playerRepository.saveAndFlush(player);
        } else if (foundRoom.getGameStatus() == GameStatus.SETTING_WORDS) {
            for (Player roomPlayer : foundRoom.getPlayers()) {
                if (roomPlayer.getWordSettingUserId().equals(player.getUser().getUserId())) {
                    roomPlayer.setWordSettingUserId(player.getWordSettingUserId());
                }
            }
            foundRoom.removePlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = roomRepository.saveAndFlush(foundRoom);
        } else if (foundRoom.getGameStatus() == GameStatus.WAITING_FOR_PLAYERS) {
            foundRoom.removePlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = roomRepository.saveAndFlush(foundRoom);
        }
        return foundRoom;
    }


    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 5)
    public Room addUserToRoom(Long userId, Long roomId, String password) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        Player player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
        if (player != null)
            throw ExceptionFactory.create(RoomError.PLAYER_ALREADY_IN_ROOM);
        List<Room> activeRooms = roomRepository.findActiveRooms(userId);
        if (activeRooms.size() >= MAX_ACTIVE_ROOMS)
            throw ExceptionFactory.create(RoomError.MAX_ACTIVE_ROOMS_NUMBER);

        Room foundRoom = roomRepository.findOne(roomId);
        if (foundRoom == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);
        if (foundRoom.getGameStatus() == GameStatus.SETTING_WORDS)
            throw ExceptionFactory.create(RoomError.GAME_ALREADY_STARTED);

        User user = userRepository.findOne(userId);
        if (user == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);

        if (foundRoom.getGameStatus() != GameStatus.GUESSING_WORDS) {
            if (foundRoom.getUserCount() == foundRoom.getMaxPlayers())
                throw ExceptionFactory.create(RoomError.ROOM_FULL);
            if (!TextUtils.isEmpty(foundRoom.getPassword()) && !TextUtils.isEmpty(password))
                if (!foundRoom.getPassword().equals(password))
                    throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
            player = conversionService.convert(user, Player.class);
            player.setStatus(PlayerStatus.ACTIVE);
            player.setAttempt(0);
            foundRoom.addPlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = roomRepository.saveAndFlush(foundRoom);
        } else {
            player = playerRepository.findPlayerByUser_UserIdAndRoom_Id(userId, roomId);
            if (player == null)
                throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER_IN_ROOM);
            player.setStatus(PlayerStatus.ACTIVE);
            playerRepository.saveAndFlush(player);
        }
        return foundRoom;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public void checkRoomPassword(Long roomId, String password) {
        if (roomId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);
        }
        Room foundRoom = roomRepository.findOne(roomId);
        if (!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(password) || !foundRoom.getPassword().equals(password))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
        }
    }
}
