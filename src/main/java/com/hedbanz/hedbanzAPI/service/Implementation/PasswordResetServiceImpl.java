package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.PasswordResetKeyWord;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.PasswordResetError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.Mail;
import com.hedbanz.hedbanzAPI.model.PasswordResetData;
import com.hedbanz.hedbanzAPI.repository.PasswordResetKeyWordRepository;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.service.EmailService;
import com.hedbanz.hedbanzAPI.service.PasswordResetService;
import com.hedbanz.hedbanzAPI.utils.KeyWordGenerator;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hedbanz.hedbanzAPI.constant.Constants.EMAIL_REGEX_PATTERN;
import static com.hedbanz.hedbanzAPI.constant.Constants.LOGIN_REGEX_PATTERN;
import static com.hedbanz.hedbanzAPI.constant.Constants.PASSWORD_REGEX_PATTERN;

@Service
public class PasswordResetServiceImpl implements PasswordResetService{
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final PasswordResetKeyWordRepository keyWordRepository;
    private final EmailService emailService;

    private final static String[] LOCALES = {"en", "ru", "uk"};

    @Autowired
    public PasswordResetServiceImpl(
            @Qualifier("MyMessageSource")MessageSource messageSource, UserRepository userRepository,
            PasswordResetKeyWordRepository keyWordRepository, EmailService emailService) {
        this.messageSource = messageSource;
        this.userRepository = userRepository;
        this.keyWordRepository = keyWordRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void generatePasswordResetKeyWord(PasswordResetData passwordResetData){
        if(TextUtils.isEmpty(passwordResetData.getLogin()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(passwordResetData.getLocale()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_LOCALE);
        if(!isValidLocale(passwordResetData.getLocale()))
            throw ExceptionFactory.create(PasswordResetError.INCORRECT_LOCALE);

        PasswordResetKeyWord oldKeyWord = keyWordRepository.findByUser_login(passwordResetData.getLogin());
        if(oldKeyWord != null)
            keyWordRepository.delete(oldKeyWord);

        User user = getUserByLoginOrEmail(passwordResetData.getLogin());

        PasswordResetKeyWord newKeyWord = new PasswordResetKeyWord();
        newKeyWord.setKeyWord(KeyWordGenerator.getGeneratedKeyWord(5));
        newKeyWord.setExpireDate(15);
        newKeyWord.setUser(user);
        keyWordRepository.save(newKeyWord);

        Mail mail = new Mail();
        mail.setFrom("no-reply@hedbanz.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Password reset request");

        Map<String, Object> model = new HashMap<>();
        model.put("keyWord", newKeyWord.getKeyWord());
        model.put("user", user);
        Locale locale = new Locale(passwordResetData.getLocale());
        model.put("greetings", messageSource.getMessage("greetings", null, locale));
        model.put("passwordResetRequested", messageSource.getMessage("password.reset.requested", null, locale));
        model.put("secretKey", messageSource.getMessage("secret.key", null, locale));
        model.put("typeSecretKey", messageSource.getMessage("type.secret.key", null, locale));
        model.put("dontResponse", messageSource.getMessage("dont.reply", null, locale));
        model.put("thank", messageSource.getMessage("thank", null, locale));
        mail.setModel(model);

        emailService.sendEmail(mail);
    }

    private boolean isValidLocale(String checkingLocale){
        for (String locale: LOCALES) {
            if(checkingLocale.equals(locale))
                return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean isValidUserKeyWord(PasswordResetData passwordResetData) {
        if(TextUtils.isEmpty(passwordResetData.getLogin()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(passwordResetData.getKeyWord()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_KEY_WORD);

        PasswordResetKeyWord keyWordFromDB = keyWordRepository.findByKeyWord(passwordResetData.getKeyWord().toUpperCase());
        if(keyWordFromDB.isExpired()){
            keyWordRepository.delete(keyWordFromDB);
            throw ExceptionFactory.create(PasswordResetError.KEY_WORD_IS_EXPIRED);
        }

        User keyWordUser = keyWordRepository.findUserByKeyWord(passwordResetData.getKeyWord().toUpperCase());
        User user = getUserByLoginOrEmail(passwordResetData.getLogin());
        if(user == null)
            throw ExceptionFactory.create(PasswordResetError.NO_SUCH_USER);

        if(keyWordUser == null)
            return false;

        if(!user.getUserId().equals(keyWordUser.getUserId()))
            return false;

        return true;
    }

    @Transactional
    public void resetUserPassword(PasswordResetData passwordResetData) {
        if(TextUtils.isEmpty(passwordResetData.getLogin()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(passwordResetData.getKeyWord()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_KEY_WORD);
        if(TextUtils.isEmpty(passwordResetData.getPassword()))
            throw ExceptionFactory.create(PasswordResetError.EMPTY_PASSWORD);

        if(!isValidUserKeyWord(passwordResetData))
            throw ExceptionFactory.create(PasswordResetError.INCORRECT_KEY_WORD);

        Pattern pattern = Pattern.compile(PASSWORD_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(passwordResetData.getPassword());
        if (!matcher.find())
            throw ExceptionFactory.create(PasswordResetError.INCORRECT_PASSWORD);

        User keyWordUser = keyWordRepository.findUserByKeyWord(passwordResetData.getKeyWord().toUpperCase());
        keyWordUser.setPassword(passwordResetData.getPassword());
        userRepository.updateUserPassword(keyWordUser.getUserId(), keyWordUser.getPassword());
        keyWordRepository.deleteByUser_login(passwordResetData.getLogin());
    }

    private User getUserByLoginOrEmail(String login) {
        Pattern pattern = Pattern.compile(LOGIN_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(login);
        if(!matcher.find()) {
            pattern = Pattern.compile(EMAIL_REGEX_PATTERN);
            matcher = pattern.matcher(login);
            if (!matcher.find())
                throw ExceptionFactory.create(PasswordResetError.INCORRECT_LOGIN);
            else
                return userRepository.findUserByEmail(login);
        }
        else
            return userRepository.findUserByLogin(login);
    }
}
