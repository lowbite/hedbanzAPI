package com.hedbanz.hedbanzAPI.services.Implementation;

import com.hedbanz.hedbanzAPI.entity.UpdateUserData;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.entity.error.LoginError;
import com.hedbanz.hedbanzAPI.entity.error.RegistrationError;
import com.hedbanz.hedbanzAPI.entity.error.UpdateError;
import com.hedbanz.hedbanzAPI.exceptions.UserException;
import com.hedbanz.hedbanzAPI.repositories.UserRepository;
import com.hedbanz.hedbanzAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    public final static String EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    @Autowired
    private UserRepository userRepository;


    public User authenticate(User user){
        if(user.getLogin() == null)
            throw new UserException(new CustomError(LoginError.EMPTY_LOGIN.getErrorCode(), LoginError.EMPTY_LOGIN.getErrorMessage()));
        if(user.getPassword() == null)
            throw new UserException(new CustomError(LoginError.EMPTY_PASSWORD.getErrorCode(), LoginError.EMPTY_PASSWORD.getErrorMessage()));

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(user.getLogin());

        User foundUser;
        if(matcher.find()) {
            foundUser = userRepository.findUserByEmail(user.getLogin());
        }else {
            foundUser = userRepository.findUserByLogin(user.getLogin());
        }
        if(foundUser != null)
            if(foundUser.getPassword().equals(user.getPassword()))
                return foundUser;
            else
                throw new UserException(new CustomError(LoginError.INCORRECT_PASSWORD.getErrorCode(),LoginError.INCORRECT_PASSWORD.getErrorMessage()));
        else
            throw new UserException(new CustomError(LoginError.NO_SUCH_USER.getErrorCode(),LoginError.NO_SUCH_USER.getErrorMessage()));
    }

    @Transactional
    public User updateUserData(UpdateUserData userData) {
        long id = userData.getId();
        String login = userData.getNewLogin();
        String newPassword = userData.getNewPassword() != "" ? userData.getNewPassword() : userData.getOldPassword();
        String oldPassword = userData.getOldPassword();

        int rowsUpdated = userRepository.updateUserData(id, login, newPassword, oldPassword);

        if(rowsUpdated != 1)
            throw new UserException(new CustomError(UpdateError.WRONG_PASSWORD.getErrorCode(), UpdateError.WRONG_PASSWORD.getErrorMessage()));

        User foundUser = userRepository.findUserByLogin(login);

        return foundUser;
    }

    public User register(User user) {
        if(user.getLogin() == null)
            throw new UserException(new CustomError(RegistrationError.EMPTY_LOGIN.getErrorCode(),RegistrationError.EMPTY_LOGIN.getErrorMessage()));
        if(user.getPassword() == null)
            throw new UserException(new CustomError(RegistrationError.EMPTY_PASSWORD.getErrorCode(),RegistrationError.EMPTY_PASSWORD.getErrorMessage()));
        if(user.getEmail() == null)
            throw new UserException(new CustomError(RegistrationError.EMPTY_EMAIL.getErrorCode(),RegistrationError.EMPTY_EMAIL.getErrorMessage()));

        User foundUser = userRepository.findUserByEmail(user.getEmail());

        if (foundUser != null)
            throw new UserException(new CustomError(RegistrationError.SUCH_EMAIL_ALREADY_USING.getErrorCode(), RegistrationError.SUCH_EMAIL_ALREADY_USING.getErrorMessage()));

        foundUser = userRepository.findUserByLogin(user.getLogin());

        if(foundUser != null)
            throw new UserException(new CustomError(RegistrationError.SUCH_LOGIN_ALREADY_EXIST.getErrorCode(), RegistrationError.SUCH_LOGIN_ALREADY_EXIST.getErrorMessage()));

            user.setImagePath("source/image.jpg");
            user.setMoney(0);
            user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
            return userRepository.saveAndFlush(user);

    }

    public User getUser(long userId){
        return userRepository.findOne(userId);
    }
}
