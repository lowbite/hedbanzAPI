package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.repository.RoomRepository;
import com.hedbanz.hedbanzAPI.security.SecurityUserDetails;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    private static final String LOGIN_REGEX = "^[a-zA-Z0-9.]{3,10}$";
    private static final String PASSWORD_REGEX = "\\S{4,14}";

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public User authenticate(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(user.getLogin());

        User foundUser;
        if (matcher.find()) {
            foundUser = userRepository.findUserByEmail(user.getLogin());
        } else {
            foundUser = userRepository.findUserByLogin(user.getLogin());
        }

        if (foundUser == null)
            throw ExceptionFactory.create(UserError.INVALID_LOGIN);
        if (!foundUser.getPassword().equals(user.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        final String token = UUID.randomUUID().toString();
        foundUser.setSecurityToken(token);
        userRepository.updateUserToken(token, foundUser.getUserId());
        return foundUser;
    }

    @Transactional(readOnly = true)
    public Optional<UserDetails> findUserByToken(String token) {
        //TODO add no token exception
        User foundUser = userRepository.findUserBySecurityToken(token);
        return Optional.ofNullable(SecurityUserDetails.from(foundUser));
    }

    @Transactional
    public void logout(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);
        Optional<User> userCandidate = Optional.ofNullable(userRepository.findUserByLogin(user.getLogin()));
        User foundUser = userCandidate.orElseThrow(() -> ExceptionFactory.create(UserError.INVALID_LOGIN));
        if (!foundUser.getPassword().equals(user.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);
        foundUser.setSecurityToken(null);
        userRepository.deleteUserToken(foundUser.getUserId());
    }

    @Transactional
    public User updateUserData(User user) {
        if (user.getUserId() == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        if (userRepository.updateUserData(user.getUserId(), user.getLogin(), user.getPassword()) != 1)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);

        return userRepository.findOne(user.getUserId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User register(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);
        if (TextUtils.isEmpty(user.getEmail()))
            throw ExceptionFactory.create(UserError.EMPTY_EMAIL);

        Pattern pattern = Pattern.compile(LOGIN_REGEX);
        Matcher matcher = pattern.matcher(user.getLogin());
        if (!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_LOGIN);
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(user.getEmail());
        if (!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_EMAIL);
        pattern = Pattern.compile(PASSWORD_REGEX);
        matcher = pattern.matcher(user.getPassword());
        if (!matcher.find())
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        User foundUser = userRepository.findUserByEmail(user.getEmail());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_EMAIL_ALREADY_USING);

        foundUser = userRepository.findUserByLogin(user.getLogin());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_LOGIN_ALREADY_EXIST);

        user.setImagePath("source/image.jpg");
        user.setMoney(0);
        final String token = UUID.randomUUID().toString();
        user.setSecurityToken(token);
        return userRepository.saveAndFlush(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUser(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        return userRepository.findOne(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByFcmTokenIsNotNull();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Friend> getUserFriends(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        Set<Friend> friends = new HashSet<>(userRepository.findPendingAndAcceptedFriends(userId));
        List<Friend> acceptedFriends = userRepository.findAcceptedFriends(userId);
        List<Friend> friendsWithRequest = userRepository.findRequestingFriends(userId);
        //Removing accepted friendDTOS object from all friendDTOS, because they have wrong flag
        friends.removeAll(acceptedFriends);
        //Adding accepted friendDTOS
        friends.addAll(acceptedFriends);
        friends.addAll(friendsWithRequest);
        return new ArrayList<>(friends);
    }

    @Transactional(readOnly = true)
    public List<Friend> getUserAcceptedFriends(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        return userRepository.findAcceptedFriends(userId);
    }

    @Transactional
    public void setUserFcmToken(User user) {
        if (userRepository.updateUserFcmToken(user.getFcmToken(), user.getUserId()) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }

    @Transactional
    public void releaseUserFcmToken(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        if (userRepository.deleteUserFcmToken(userId) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        User friend = userRepository.findOne(friendId);
        User user = userRepository.findOne(userId);

        if (!user.addFriend(friend))
            throw ExceptionFactory.create(UserError.ALREADY_FRIENDS);

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        User friend = userRepository.findOne(friendId);
        User user = userRepository.findOne(userId);

        if (!user.removeFriend(friend))
            throw ExceptionFactory.create(UserError.NOT_FRIENDS);

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void addInvite(Long userId, Long roomId) {
        if(userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if(roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);

        User user = userRepository.findOne(userId);
        if(user == null)
            throw ExceptionFactory.create(UserError.NO_SUCH_USER);

        Room room = roomRepository.findOne(roomId);
        if(room == null)
            throw ExceptionFactory.create(RoomError.NO_SUCH_ROOM);

        user.addInvite(room);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public List<Friend> getAcceptedFriendsInRoom(Long userId, Long roomId){
        if(userId == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if(roomId == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_ROOM_ID);

        List<Friend> allFriends = new LinkedList<>(userRepository.findAcceptedFriends(userId));
        List<Friend> invitedFriends = new ArrayList<>(userRepository.findAcceptedFriendsWithInvitesToRoom(userId, roomId));
        List<Friend> friendsInRoom = new ArrayList<>(roomRepository.findAcceptedFriendsInRoom(userId, roomId));
        allFriends.removeAll(invitedFriends);
        allFriends.removeAll(friendsInRoom);
        allFriends.addAll(invitedFriends);
        allFriends.addAll(friendsInRoom);
        return allFriends.stream().distinct().collect(Collectors.toList());
    }
}
