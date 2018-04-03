package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.DTO.FriendDTO;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserUpdateDTO;
import com.hedbanz.hedbanzAPI.entity.error.UserError;
import com.hedbanz.hedbanzAPI.exception.UserException;
import com.hedbanz.hedbanzAPI.repository.CRUDUserRepository;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    private static final String LOGIN_REGEX = "^[a-zA-Z0-9.]{3,10}$";
    private static final String PASSWORD_REGEX = "\\S{4,14}";

    @Autowired
    private CRUDUserRepository CRUDUserRepository;

    @Autowired
    @Qualifier("APIConversionService")
    private ConversionService conversionService;

    public UserDTO authenticate(UserDTO userDTO){
        if(TextUtils.isEmpty(userDTO.getLogin()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_LOGIN.getErrorCode(),
                    UserError.EMPTY_LOGIN.getErrorMessage()));
        if(TextUtils.isEmpty(userDTO.getPassword()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_PASSWORD.getErrorCode(),
                    UserError.EMPTY_PASSWORD.getErrorMessage()));

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(userDTO.getLogin());

        User foundUser;
        if(matcher.find()) {
            foundUser = CRUDUserRepository.findUserByEmail(userDTO.getLogin());
        }else {
            foundUser = CRUDUserRepository.findUserByLogin(userDTO.getLogin());
        }

        if(foundUser == null)
            throw new UserException(new CustomError(
                    UserError.NO_SUCH_USER.getErrorCode(),
                    UserError.NO_SUCH_USER.getErrorMessage()));
        if(!foundUser.getPassword().equals(userDTO.getPassword()))
            throw new UserException(new CustomError(
                    UserError.INCORRECT_PASSWORD.getErrorCode(),
                    UserError.INCORRECT_PASSWORD.getErrorMessage()));

        return conversionService.convert(foundUser, UserDTO.class);
    }

    @Transactional
    public UserDTO updateUserData(UserUpdateDTO userData) {
        if(userData.getId() == null)
            throw new UserException( new CustomError(
                    UserError.INCORRECT_USER_ID.getErrorCode(),
                    UserError.INCORRECT_USER_ID.getErrorMessage()));
        if(TextUtils.isEmpty(userData.getLogin()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_LOGIN.getErrorCode(),
                    UserError.EMPTY_LOGIN.getErrorMessage()));
        if(TextUtils.isEmpty(userData.getOldPassword()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_EMAIL.getErrorCode(),
                    UserError.EMPTY_EMAIL.getErrorMessage()));

        long id = userData.getId();
        String login = userData.getLogin();
        String newPassword = TextUtils.isEmpty(userData.getNewPassword()) ? userData.getNewPassword() : userData.getOldPassword();

        int rowsUpdated = CRUDUserRepository.updateUserData(id, login, newPassword);

        if(rowsUpdated != 1)
            throw new UserException(new CustomError(
                    UserError.INCORRECT_USER_ID.getErrorCode(),
                    UserError.INCORRECT_USER_ID.getErrorMessage()));

        User user = CRUDUserRepository.findUserByLogin(login);
        return conversionService.convert(user, UserDTO.class);
    }

    public UserDTO register(UserDTO userDTO) {
        if(TextUtils.isEmpty(userDTO.getLogin()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_LOGIN.getErrorCode(),
                    UserError.EMPTY_LOGIN.getErrorMessage()));
        if(TextUtils.isEmpty(userDTO.getPassword()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_PASSWORD.getErrorCode(),
                    UserError.EMPTY_PASSWORD.getErrorMessage()));
        if(TextUtils.isEmpty(userDTO.getEmail()))
            throw new UserException(new CustomError(
                    UserError.EMPTY_EMAIL.getErrorCode(),
                    UserError.EMPTY_EMAIL.getErrorMessage()));

        Pattern pattern = Pattern.compile(LOGIN_REGEX);
        Matcher matcher = pattern.matcher(userDTO.getLogin());
        if(!matcher.find())
            throw new UserException(new CustomError(
                    UserError.INVALID_LOGIN.getErrorCode(),
                    UserError.INVALID_LOGIN.getErrorMessage()));
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(userDTO.getEmail());
        if(!matcher.find())
            throw new UserException(new CustomError(
                    UserError.INVALID_EMAIL.getErrorCode(),
                    UserError.INVALID_EMAIL.getErrorMessage()));
        pattern = Pattern.compile(PASSWORD_REGEX);
        matcher = pattern.matcher(userDTO.getPassword());
        if(!matcher.find())
            throw new UserException(new CustomError(
                    UserError.INVALID_PASSWORD.getErrorCode(),
                    UserError.INCORRECT_PASSWORD.getErrorMessage()));

        User foundUser = CRUDUserRepository.findUserByEmail(userDTO.getEmail());

        if (foundUser != null)
            throw new UserException(new CustomError(
                    UserError.SUCH_EMAIL_ALREADY_USING.getErrorCode(),
                    UserError.SUCH_EMAIL_ALREADY_USING.getErrorMessage()));

        foundUser = CRUDUserRepository.findUserByLogin(userDTO.getLogin());

        if(foundUser != null)
            throw new UserException(new CustomError(
                    UserError.SUCH_LOGIN_ALREADY_EXIST.getErrorCode(),
                    UserError.SUCH_LOGIN_ALREADY_EXIST.getErrorMessage()));

        userDTO.setImagePath("source/image.jpg");
        userDTO.setMoney(0);
        foundUser = CRUDUserRepository.saveAndFlush(conversionService.convert(userDTO, User.class));
        return conversionService.convert(foundUser, UserDTO.class);
    }

    public UserDTO getUser(long userId){
        User userDTO = CRUDUserRepository.findOne(userId);
        return conversionService.convert(userDTO, UserDTO.class);
    }

    public List<FriendDTO> getUserFriends(long userId){
        List<FriendDTO> friends = CRUDUserRepository.getAllFriends(userId);
        List<FriendDTO> acceptedFriends = CRUDUserRepository.getAcceptedFriends(userId);
        //Removing accepted friendDTOS object from all friendDTOS, because they have wrong flag
        friends.removeAll(acceptedFriends);
        //Adding accepted friendDTOS
        friends.addAll(acceptedFriends);
        return friends;
    }

    public void setUserToken(long userId, String token) {
        if(CRUDUserRepository.updateUserToken(token, userId) == 0)
            throw new UserException(new CustomError(
                    UserError.INCORRECT_USER_ID.getErrorCode(),
                    UserError.INCORRECT_USER_ID.getErrorMessage()));
    }

    public void releaseUserToken(long userId) {
        if(CRUDUserRepository.deleteUserToken(userId) == 0)
            throw new UserException(new CustomError(
                    UserError.INCORRECT_USER_ID.getErrorCode(),
                    UserError.INCORRECT_USER_ID.getErrorMessage()));
    }
}
