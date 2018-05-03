package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserUpdateDTO;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CrudUserRepository;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    private static final String LOGIN_REGEX = "^[a-zA-Z0-9.]{3,10}$";
    private static final String PASSWORD_REGEX = "\\S{4,14}";

    private final CrudUserRepository CrudUserRepository;

    private final ConversionService conversionService;

    @Autowired
    public UserServiceImpl(CrudUserRepository CrudUserRepository, @Qualifier("APIConversionService") ConversionService conversionService) {
        this.CrudUserRepository = CrudUserRepository;
        this.conversionService = conversionService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserDTO authenticate(UserDTO userDTO){
        if(TextUtils.isEmpty(userDTO.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(userDTO.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(userDTO.getLogin());

        User foundUser;
        if(matcher.find()) {
            foundUser = CrudUserRepository.findUserByEmail(userDTO.getLogin());
        }else {
            foundUser = CrudUserRepository.findUserByLogin(userDTO.getLogin());
        }

        if(foundUser == null)
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);
        if(!foundUser.getPassword().equals(userDTO.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        return conversionService.convert(foundUser, UserDTO.class);
    }

    @Transactional
    public UserDTO updateUserData(UserUpdateDTO userData) {
        if(userData.getId() == null)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
        if(TextUtils.isEmpty(userData.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(userData.getOldPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_EMAIL);

        long id = userData.getId();
        String login = userData.getLogin();
        String newPassword = TextUtils.isEmpty(userData.getNewPassword()) ? userData.getNewPassword() : userData.getOldPassword();

        int rowsUpdated = CrudUserRepository.updateUserData(id, login, newPassword);

        if(rowsUpdated != 1)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);

        User user = CrudUserRepository.findUserByLogin(login);
        return conversionService.convert(user, UserDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDTO register(UserDTO userDTO) {
        if(TextUtils.isEmpty(userDTO.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(userDTO.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);
        if(TextUtils.isEmpty(userDTO.getEmail()))
            throw ExceptionFactory.create(UserError.EMPTY_EMAIL);

        Pattern pattern = Pattern.compile(LOGIN_REGEX);
        Matcher matcher = pattern.matcher(userDTO.getLogin());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_LOGIN);
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(userDTO.getEmail());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INVALID_EMAIL);
        pattern = Pattern.compile(PASSWORD_REGEX);
        matcher = pattern.matcher(userDTO.getPassword());
        if(!matcher.find())
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);

        User foundUser = CrudUserRepository.findUserByEmail(userDTO.getEmail());

        if (foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_EMAIL_ALREADY_USING);

        foundUser = CrudUserRepository.findUserByLogin(userDTO.getLogin());

        if(foundUser != null)
            throw ExceptionFactory.create(UserError.SUCH_LOGIN_ALREADY_EXIST);

        userDTO.setImagePath("source/image.jpg");
        userDTO.setMoney(0);
        foundUser = CrudUserRepository.saveAndFlush(conversionService.convert(userDTO, User.class));
        return conversionService.convert(foundUser, UserDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserDTO getUser(long userId){
        User userDTO = CrudUserRepository.findOne(userId);
        return conversionService.convert(userDTO, UserDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<FriendDTO> getUserFriends(long userId){
        List<FriendDTO> friends = CrudUserRepository.getAllFriends(userId);
        List<FriendDTO> acceptedFriends = CrudUserRepository.getAcceptedFriends(userId);
        //Removing accepted friendDTOS object from all friendDTOS, because they have wrong flag
        friends.removeAll(acceptedFriends);
        //Adding accepted friendDTOS
        friends.addAll(acceptedFriends);
        return friends;
    }

    public void setUserToken(long userId, String token) {
        if(CrudUserRepository.updateUserToken(token, userId) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }

    public void releaseUserToken(long userId) {
        if(CrudUserRepository.deleteUserToken(userId) == 0)
            throw ExceptionFactory.create(UserError.INCORRECT_USER_ID);
    }
}
