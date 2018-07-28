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
import com.hedbanz.hedbanzAPI.model.RoomFilterSpecification;
import com.hedbanz.hedbanzAPI.repository.*;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.model.RoomFilter;
import com.hedbanz.hedbanzAPI.model.Word;
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
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if (room.getName() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        if(room.getIconId() == null)
            throw ExceptionFactory.create(RoomError.EMPTY_ICON_ID);
        if(room.getStickerId() == null)
            throw ExceptionFactory.create(RoomError.EMPTY_SITCKER_ID);

        List<Room> rooms = roomRepository.findRoomsByName(room.getName());
        if(rooms.size() != 0)
            throw ExceptionFactory.create(RoomError.SUCH_ROOM_ALREADY_EXIST);

        User user = userRepository.findOne(creatorId);
        if (user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);
        List<Room> activeRooms = roomRepository.findActiveRooms(creatorId);
        if (activeRooms.size() >= MAX_ACTIVE_ROOMS)
            throw ExceptionFactory.create(RoomError.PLAYER_HAS_ACTIVE_ROOMS_MAX_NUMBER);
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
        Room room = roomRepository.findOne(roomId);
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
        Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
    }

    @Transactional
    public Room setPlayersWordSetters(Long roomId) {
        if (roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Room room = roomRepository.findOne(roomId);
        if (room == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }

        List<Player> players = room.getPlayers();
        Player player;
        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
            if (i + 1 < players.size()) {
                player.setWordSettingUserId(players.get(i + 1).getUser().getUserId());
            } else {
                player.setWordSettingUserId(players.get(0).getUser().getUserId());
            }
        }
        return roomRepository.saveAndFlush(room);
    }

    @Override
    public Room setGameOverStatus(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        room.setGameStatus(GameStatus.GAME_OVER);
        return roomRepository.saveAndFlush(room);
    }

    @Cacheable("rooms")
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<Room> page = roomRepository.findAllRooms(pageable);
        return page.getContent();
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(Long userId) {/*
        List<Player> players = playerRepository.findPlayersByUserId(userId);
        return players.stream().map(Player::getRoom).collect(Collectors.toList());*/
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
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);

        return roomRepository.findAll(new RoomFilterSpecification(roomFilter, userId));
    }

    @CacheEvict(value = "rooms", allEntries = true)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Room leaveFromRoom(Long userId, Long roomId) {
        if (userId == null || roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        User user = userRepository.findOne(userId);
        if (user == null)
            throw ExceptionFactory.create(RoomError.WRONG_USER);

        Room foundRoom = roomRepository.findOne(roomId);
        Player player = foundRoom.getPlayerByLogin(user.getLogin());
        if (player == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);

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
        if (userId == null || roomId == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }
        List<Room> activeRooms = roomRepository.findActiveRooms(userId);
        if (activeRooms.size() >= MAX_ACTIVE_ROOMS)
            throw ExceptionFactory.create(RoomError.PLAYER_HAS_ACTIVE_ROOMS_MAX_NUMBER);

        Room foundRoom = roomRepository.findOne(roomId);
        if (foundRoom.getGameStatus() == GameStatus.SETTING_WORDS)
            throw ExceptionFactory.create(RoomError.GAME_HAS_BEEN_ALREADY_STARTED);

        User user = userRepository.findOne(userId);
        if (user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);

        if (foundRoom.getGameStatus() != GameStatus.GUESSING_WORDS) {
            if (foundRoom.getUserCount() == foundRoom.getMaxPlayers())
                throw ExceptionFactory.create(RoomError.ROOM_FULL);
            if (!TextUtils.isEmpty(foundRoom.getPassword()) && !TextUtils.isEmpty(password))
                if (!foundRoom.getPassword().equals(password))
                    throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
            Player player = conversionService.convert(user, Player.class);
            player.setStatus(PlayerStatus.ACTIVE);
            player.setAttempt(0);
            foundRoom.addPlayer(player);
            foundRoom.setCurrentPlayersNumber(foundRoom.getPlayers().size());
            foundRoom = roomRepository.saveAndFlush(foundRoom);
        } else {
            Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
            if (player == null)
                throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
            player.setStatus(PlayerStatus.ACTIVE);
            playerRepository.saveAndFlush(player);
        }
        return foundRoom;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player setPlayerStatus(Long userId, Long roomId, PlayerStatus status) {
        if (userId == null || roomId == null || status == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        Player player = playerRepository.findPlayerByUserIdAndRoomId(userId, roomId);
        if (player == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        }
        if (player.getStatus().equals(status)) {
            return player;
        }
        player.setStatus(status);
        return playerRepository.saveAndFlush(player);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public void checkRoomPassword(Long roomId, String password) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        Room foundRoom = roomRepository.findOne(roomId);
        if (!TextUtils.isEmpty(foundRoom.getPassword())) {
            if (TextUtils.isEmpty(password) || !foundRoom.getPassword().equals(password))
                throw ExceptionFactory.create(RoomError.WRONG_PASSWORD);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean startGame(Long roomId) {
        Room room = roomRepository.getOne(roomId);
        if (room.getGameStatus() != GameStatus.WAITING_FOR_PLAYERS)
            return false;
        room.setGameStatus(GameStatus.SETTING_WORDS);
        roomRepository.saveAndFlush(room);
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setPlayerWord(Word word) {
        if (word.getSenderId() == null || word.getWord() == null || word.getRoomId() == null) {
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);
        }

        /*Player wordReceiverPlayer = null;
        List<Player> players = playerRepository.findPlayersByRoomId(word.getRoomId());
        for (Player player : players) {
            if (player.getUser().getUserId().equals(word.getWordReceiverId())) {
                player.setWord(word.getWord());
                wordReceiverPlayer = player;
                break;
            }
        }

        if (wordReceiverPlayer == null) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUser().getUserId().equals(word.getSenderId())) {
                    if (i + 1 < players.size()) {
                        if (players.get(i + 1).getWord() == null) {
                            players.get(i + 1).setWord(word.getWord());
                            wordReceiverPlayer = players.get(i + 1);
                        } else {
                            if (players.get(0).getWord() == null) {
                                players.get(0).setWord(word.getWord());
                                wordReceiverPlayer = players.get(0);
                            }
                        }
                    }
                }
            }
        }
        if (wordReceiverPlayer != null)
            playerRepository.saveAndFlush(wordReceiverPlayer);*/

        Player wordSetter = playerRepository.findPlayerByUserIdAndRoomId(word.getSenderId(), word.getRoomId());
        if (wordSetter == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        Player wordReceiver = playerRepository.findPlayerByUserIdAndRoomId(wordSetter.getWordSettingUserId(), word.getRoomId());
        if (wordReceiver == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_USER_IN_ROOM);
        wordReceiver.setWord(word.getWord());
        playerRepository.saveAndFlush(wordReceiver);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Player startGuessing(Long roomId) {
        List<Player> players = playerRepository.findPlayersByRoomId(roomId);
        if (players == null) {
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        }
        Player player = playerRepository.findOne(players.get(0).getId());
        player.setAttempt(1);
        Room room = roomRepository.findOne(roomId);
        room.setGameStatus(GameStatus.GUESSING_WORDS);
        roomRepository.saveAndFlush(room);
        playerRepository.saveAndFlush(player);
        return players.get(0);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Player getNextGuessingPlayer(Long roomId) {
        List<Player> players = playerRepository.findPlayersByRoomId(roomId);
        if (players == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);
        /*
        int attempt;
        int i = 0;

        while (i < players.size()) {
            attempt = players.get(i).getAttempt();
            if (attempt != 0) {
                resultPlayer = players.get(i);
                if (attempt < MAX_GUESS_ATTEMPTS) {
                    if (resultPlayer.getStatus() != PlayerStatus.ACTIVE) {
                        playerRepository.updatePlayerAttempts(0, resultPlayer.getId());
                        resultPlayer = getNextGuessingPlayerAfterCurrentPlayer(players, i);
                    } else {
                        resultPlayer.setAttempt(resultPlayer.getAttempt() + 1);
                    }
                    break;
                } else {
                    playerRepository.updatePlayerAttempts(0, resultPlayer.getId());
                    resultPlayer = getNextGuessingPlayerAfterCurrentPlayer(players, i);
                    break;
                }
            }
            i++;
        }

        if (resultPlayer == null) {
            resultPlayer = players.get(0);
            resultPlayer.setAttempt(1);
        }

        if (isLastGuessingPlayer(players, resultPlayer.getId())) {
            resultPlayer.setAttempt(-1);
        }*/

        Player currentGuessingPlayer = null;
        Player nextGuessingPLayer = null;
        int currentGuessingPlayerIndex = 0;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getAttempt() != 0){
                currentGuessingPlayer = players.get(i);
                currentGuessingPlayerIndex = i;
            }
        }
        if (currentGuessingPlayer == null) {
            nextGuessingPLayer = players.get(0);
            nextGuessingPLayer.setAttempt(1);
            if(nextGuessingPLayer.getIsWinner()){
                nextGuessingPLayer = getNextGuessingPlayerAfterCurrentPlayer(players, 0);
            }
        }else if (currentGuessingPlayer.getAttempt() < MAX_GUESS_ATTEMPTS){
            if (currentGuessingPlayer.getStatus() != PlayerStatus.ACTIVE) {
                playerRepository.updatePlayerAttempts(0, currentGuessingPlayer.getId());
                nextGuessingPLayer = getNextGuessingPlayerAfterCurrentPlayer(players, currentGuessingPlayerIndex);
            } else {
                nextGuessingPLayer = currentGuessingPlayer;
                nextGuessingPLayer.setAttempt(nextGuessingPLayer.getAttempt() + 1);
            }
        } else {
            playerRepository.updatePlayerAttempts(0, currentGuessingPlayer.getId());
            nextGuessingPLayer = getNextGuessingPlayerAfterCurrentPlayer(players, currentGuessingPlayerIndex);
        }
        if (isLastGuessingPlayer(players, nextGuessingPLayer.getId())) {
            nextGuessingPLayer.setAttempt(-1);
        }
        playerRepository.updatePlayerAttempts(nextGuessingPLayer.getAttempt(), nextGuessingPLayer.getId());
        return (Player) nextGuessingPLayer.clone();
    }

    private Player getNextGuessingPlayerAfterCurrentPlayer(List<Player> players, int currentPlayerIndex) {
        Player nextPlayer;
        for (int i = currentPlayerIndex + 1; i < players.size(); i++) {
            nextPlayer = players.get(i);
            if (nextPlayer.getStatus() == PlayerStatus.ACTIVE && !nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }

        for (int i = 0; i < currentPlayerIndex; i++) {
            nextPlayer = players.get(i);
            if (nextPlayer.getStatus() == PlayerStatus.ACTIVE && !nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }
        for (int i = currentPlayerIndex + 1; i < players.size(); i++) {
            nextPlayer = players.get(i);
            if (!nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }

        for (int i = 0; i < currentPlayerIndex; i++) {
            nextPlayer = players.get(i);
            if (!nextPlayer.getIsWinner()) {
                nextPlayer.setAttempt(1);
                return nextPlayer;
            }
        }
        return players.get(currentPlayerIndex);
    }

    private boolean isLastGuessingPlayer(List<Player> players, long currentPlayerId) {
        for (Player player : players) {
            if (!player.getIsWinner() && player.getId() != currentPlayerId)
                return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isGameOver(Long roomId) {
        if (roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);

        Room room = roomRepository.findOne(roomId);
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

        Room room = roomRepository.findOne(roomId);
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
        return roomRepository.saveAndFlush(room);
    }
}
