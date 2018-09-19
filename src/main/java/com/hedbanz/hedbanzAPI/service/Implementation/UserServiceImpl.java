package com.hedbanz.hedbanzAPI.service.Implementation;

import static com.hedbanz.hedbanzAPI.constant.Constants.*;

import com.hedbanz.hedbanzAPI.constant.RoleName;
import com.hedbanz.hedbanzAPI.entity.Role;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.repository.FeedbackRepository;
import com.hedbanz.hedbanzAPI.repository.RoleRepository;
import com.hedbanz.hedbanzAPI.repository.RoomRepository;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoomRepository roomRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void logout(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(InputError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(InputError.EMPTY_PASSWORD);
        Optional<User> userCandidate = Optional.ofNullable(userRepository.findUserByLogin(user.getLogin()));
        User foundUser = userCandidate.orElseThrow(() -> ExceptionFactory.create(InputError.INCORRECT_CREDENTIALS));
        if (!foundUser.getPassword().equals(user.getPassword()))
            throw ExceptionFactory.create(InputError.INCORRECT_CREDENTIALS);
    }

    @Transactional
    public User updateUserInfo(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(InputError.EMPTY_LOGIN);
        if (user.getMoney() == null && user.getIconId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_UPDATE_INFO);
        User retrievedUser = userRepository.findUserByLogin(user.getLogin());
        if (user.getMoney() != null)
            retrievedUser.setMoney(user.getMoney());
        if (user.getIconId() != null)
            retrievedUser.setIconId(user.getIconId());

        return userRepository.save(retrievedUser);
    }

    @Transactional
    public Long getUserNumber() {
        return userRepository.findUsersCount();
    }

    @Transactional
    public User updateUserData(User user) {
        if (user.getUserId() == null)
            throw ExceptionFactory.create(InputError.INCORRECT_USER_ID);
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(InputError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(InputError.EMPTY_PASSWORD);

        User retrievedUser = userRepository.findOne(user.getUserId());
        if (retrievedUser == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);
        retrievedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        retrievedUser.setLogin(user.getLogin());
        return userRepository.saveAndFlush(retrievedUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User register(User user) {
        if (TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(InputError.EMPTY_LOGIN);
        if (TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(InputError.EMPTY_PASSWORD);
        if (TextUtils.isEmpty(user.getEmail()))
            throw ExceptionFactory.create(InputError.EMPTY_EMAIL);

        Pattern pattern = Pattern.compile(LOGIN_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(user.getLogin());
        if (!matcher.find())
            throw ExceptionFactory.create(InputError.INVALID_LOGIN);
        pattern = Pattern.compile(EMAIL_REGEX_PATTERN);
        matcher = pattern.matcher(user.getEmail());
        if (!matcher.find())
            throw ExceptionFactory.create(InputError.INVALID_EMAIL);
        pattern = Pattern.compile(PASSWORD_REGEX_PATTERN);
        matcher = pattern.matcher(user.getPassword());
        if (!matcher.find())
            throw ExceptionFactory.create(InputError.INVALID_PASSWORD);

        User foundUser = userRepository.findUserByEmail(user.getEmail());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_EMAIL_ALREADY_USING);

        foundUser = userRepository.findUserByLogin(user.getLogin());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_LOGIN_ALREADY_USING);

        Role role = roleRepository.findByName(RoleName.ROLE_USER);
        User newUser = User.Builder()
                .setLogin(user.getLogin())
                .setPassword(passwordEncoder.encode(user.getPassword()))
                .setEmail(user.getEmail())
                .setRoles(Collections.singleton(role))
                .build();
        return userRepository.saveAndFlush(newUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUser(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        return userRepository.findOne(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByFcmTokenIsNotNull();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Friend> getUserFriends(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
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
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        return userRepository.findAcceptedFriends(userId);
    }

    @Transactional
    public Long getFriendsNumber(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }

        return userRepository.countFriends(userId);
    }

    @Override
    public List<String> getAllFcmTokens() {
        return userRepository.findAllFcmTokens();
    }

    @Transactional
    public void setUserFcmToken(Long userId, String fcmToken) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (TextUtils.isEmpty(fcmToken)) {
            throw ExceptionFactory.create(InputError.EMPTY_FCM_TOKEN);
        }
        if (userRepository.updateUserFcmToken(fcmToken, userId) == 0)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);
    }

    @Transactional
    public void releaseUserFcmToken(Long userId) {
        if (userId == null) {
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        }
        if (userRepository.deleteUserFcmToken(userId) == 0)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        User friend = userRepository.findOne(friendId);
        User user = userRepository.findOne(userId);

        if (!user.addFriend(friend))
            throw ExceptionFactory.create(UserError.ALREADY_FRIENDS);

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void declineFriendship(Long userId, Long friendId) {
        if (userId == null || friendId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        User friend = userRepository.findOne(friendId);
        User user = userRepository.findOne(userId);

        if (!user.removeFriend(friend))
            throw ExceptionFactory.create(UserError.NOT_FRIENDS);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        User friend = userRepository.findOne(friendId);
        User user = userRepository.findOne(userId);

        if (!user.removeFriend(friend))
            throw ExceptionFactory.create(UserError.NOT_FRIENDS);
        if (!friend.removeFriend(user))
            throw ExceptionFactory.create(UserError.NOT_FRIENDS);

        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(friend);
    }

    @Transactional
    public void addInvite(Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);

        User user = userRepository.findOne(userId);
        if (user == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);

        Room room = roomRepository.findOne(roomId);
        if (room == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_ROOM);
        room.addInvitedUser(user);
        roomRepository.save(room);
    }

    @Transactional
    public List<Friend> getAcceptedFriendsInRoom(Long userId, Long roomId) {
        if (userId == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if (roomId == null)
            throw ExceptionFactory.create(InputError.EMPTY_ROOM_ID);

        List<Friend> allFriends = new LinkedList<>(userRepository.findAcceptedFriends(userId));
        List<Friend> invitedFriends = new ArrayList<>(userRepository.findAcceptedFriendsWithInvitesToRoom(userId, roomId));
        List<Friend> friendsInRoom = new ArrayList<>(userRepository.findFriendsInRoom(userId, roomId));
        allFriends.removeAll(invitedFriends);
        allFriends.addAll(invitedFriends);
        allFriends.removeAll(friendsInRoom);
        allFriends.addAll(friendsInRoom);
        return allFriends.stream().distinct().collect(Collectors.toList());
    }
}
