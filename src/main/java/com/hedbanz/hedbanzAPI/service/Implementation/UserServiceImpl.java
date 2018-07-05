package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.security.SecurityUserDetails;
import com.hedbanz.hedbanzAPI.model.Friend;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CrudUserRepository;
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

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    private static final String LOGIN_REGEX = "^[a-zA-Z0-9.]{3,10}$";
    private static final String PASSWORD_REGEX = "\\S{4,14}";

    private final CrudUserRepository crudUserRepository;

    @Autowired
    public UserServiceImpl(CrudUserRepository CrudUserRepository) {
        this.crudUserRepository = CrudUserRepository;
    }

    @Transactional
    public User authenticate(User user){
        if(TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(user.getLogin());

        User foundUser;
        if(matcher.find()) {
            foundUser = crudUserRepository.findUserByEmail(user.getLogin());
        }else {
            foundUser = crudUserRepository.findUserByLogin(user.getLogin());
        }

        if(foundUser == null)
            throw ExceptionFactory.create(UserError.INVALID_LOGIN);
        if(!foundUser.getPassword().equals(user.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        final String token = UUID.randomUUID().toString();
        foundUser.setSecurityToken(token);
        crudUserRepository. updateUserToken(token, foundUser.getId());
        return foundUser;
    }

    @Transactional(readOnly = true)
    public Optional<UserDetails> findUserByToken(String token) {
        //TODO add no token exception
        User foundUser = crudUserRepository.findUserBySecurityToken(token);
        return Optional.ofNullable(SecurityUserDetails.from(foundUser));
    }

    @Transactional
    public void logout(User user) {
        if(TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);
        Optional<User> userCandidate = Optional.ofNullable(crudUserRepository.findUserByLogin(user.getLogin()));
        userCandidate.orElseThrow(()-> ExceptionFactory.create(UserError.INVALID_LOGIN));
        User foundUser = userCandidate.get();
        if(!foundUser.getPassword().equals(user.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);
        foundUser.setSecurityToken(null);
        crudUserRepository.deleteUserToken(foundUser.getId());
    }

    @Transactional
    public User updateUserData(User user) {
        if(user.getId() == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if(TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        if(crudUserRepository.updateUserData(user.getId(), user.getLogin(), user.getPassword()) != 1)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);

        return crudUserRepository.findOne(user.getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User register(User user) {
        if(TextUtils.isEmpty(user.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(user.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);
        if(TextUtils.isEmpty(user.getEmail()))
            throw ExceptionFactory.create(UserError.EMPTY_EMAIL);

        Pattern pattern = Pattern.compile(LOGIN_REGEX);
        Matcher matcher = pattern.matcher(user.getLogin());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_LOGIN);
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(user.getEmail());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_EMAIL);
        pattern = Pattern.compile(PASSWORD_REGEX);
        matcher = pattern.matcher(user.getPassword());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        User foundUser = crudUserRepository.findUserByEmail(user.getEmail());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_EMAIL_ALREADY_USING);

        foundUser = crudUserRepository.findUserByLogin(user.getLogin());

        if(foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_LOGIN_ALREADY_EXIST);

        user.setImagePath("source/image.jpg");
        user.setMoney(0);
        return crudUserRepository.saveAndFlush(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public User getUser(Long userId){
        if(userId == null){
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }return crudUserRepository.findOne(userId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Friend> getUserFriends(Long userId){
        if(userId == null){
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        Set<Friend> friends = new HashSet<>();
        crudUserRepository.findPendingAndAcceptedFriends(userId);
        List<Friend> acceptedFriends = crudUserRepository.findAcceptedFriends(userId);
        List<Friend> friendsWithRequest = crudUserRepository.findRequestingFriends(userId);
        //Removing accepted friendDTOS object from all friendDTOS, because they have wrong flag
        friends.removeAll(acceptedFriends);
        //Adding accepted friendDTOS
        friends.addAll(acceptedFriends);
        friends.addAll(friendsWithRequest);
        return new ArrayList<>(friends);
    }

    @Transactional(readOnly = true)
    public List<Friend> getUserAcceptedFriends(Long userId) {
        if(userId == null){
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        return crudUserRepository.findAcceptedFriends(userId);
    }

    @Transactional
    public void setUserFcmToken(User user) {
        if(crudUserRepository.updateUserFcmToken(user.getFcmToken(), user.getId()) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }

    @Transactional
    public void releaseUserFcmToken(Long userId) {
        if(userId == null){
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        }
        if(crudUserRepository.deleteUserFcmToken(userId) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId){
        User friend = crudUserRepository.findOne(friendId);
        User user = crudUserRepository.findOne(userId);

        if(!user.addFriend(friend))
            throw ExceptionFactory.create(UserError.ALREADY_FRIENDS);

        crudUserRepository.saveAndFlush(user);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User friend = crudUserRepository.findOne(friendId);
        User user = crudUserRepository.findOne(userId);

        if(!user.removeFriend(friend))
            throw ExceptionFactory.create(UserError.NOT_FRIENDS);

        crudUserRepository.saveAndFlush(user);
    }
}
