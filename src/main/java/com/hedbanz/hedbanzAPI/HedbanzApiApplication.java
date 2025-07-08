package com.hedbanz.hedbanzAPI;

import com.hedbanz.hedbanzAPI.converter.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class HedbanzApiApplication {

    @Bean
    public UserToFriendConverter userToFriendConverter() {
        return new UserToFriendConverter();
    }

    @Bean
    public RoomToRoomDtoConverter roomDTOToRoomConverter() {
        return new RoomToRoomDtoConverter();
    }

    @Bean
    public RoomDtoToRoomConverter roomToRoomDTOConverter() {
        return new RoomDtoToRoomConverter();
    }

    @Bean
    public UserDtoToUserConverter userDtoToUserConverter() {
        return new UserDtoToUserConverter();
    }

    @Bean
    public UserToUserDtoConverter userToUserDtoConverter() {
        return new UserToUserDtoConverter();
    }

    @Bean
    public MessageDtoToMessageConverter messageDTOToMessageConverter() {
        return new MessageDtoToMessageConverter(userDtoToUserConverter());
    }

    @Bean
    public MessageToPushMessageDtoConverter messageToMessageNotificationDtoConverter() {
        return new MessageToPushMessageDtoConverter();
    }

    @Bean
    public MessageToMessageDtoConverter messageToMessageDTOConverter() {
        return new MessageToMessageDtoConverter(userToUserDtoConverter());
    }

    @Bean
    public MessageToSetWordDtoConverter messageToWordSettingDtoConverter() {
        return new MessageToSetWordDtoConverter(userToUserDtoConverter());
    }

    @Bean
    public UserToPlayerConverter userToPlayerConverter() {
        return new UserToPlayerConverter();
    }

    @Bean
    public PlayerToUserDtoConverter playerToUserDTOConverter() {
        return new PlayerToUserDtoConverter();
    }

    @Bean
    public PlayerToPlayerDtoConverter playerToPlayerDTOConverter() {
        return new PlayerToPlayerDtoConverter();
    }

    @Bean
    public PlayerDtoToPlayerConverter playerDtoToPlayerConverter() {
        return new PlayerDtoToPlayerConverter();
    }

    @Bean
    public MessageToQuestionDtoConverter questionToQuestionDTOConversion() {
        return new MessageToQuestionDtoConverter(playerToPlayerDTOConverter(), userToUserDtoConverter());
    }

    @Bean
    public UserUpdateDtoToUserConverter userUpdateDtoToUserConverter() {
        return new UserUpdateDtoToUserConverter();
    }

    @Bean
    public QuestionToQuestionDtoConverter questionToQuestionDtoConverter() {
        return new QuestionToQuestionDtoConverter(playerToPlayerDTOConverter());
    }

    @Bean
    public FeedbackDtoToFeedbackConverter feedbackDtoToFeedbackConverter() {
        return new FeedbackDtoToFeedbackConverter();
    }

    @Bean
    FeedbackToFeedbackDtoConverter feedbackToFeedbackDtoConverter() {
        return new FeedbackToFeedbackDtoConverter();
    }

    @Bean(name = "APIConversionService")
    public ConversionService getConversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        Set<Converter> converters = new HashSet<>();

        //add the converter
        converters.add(roomDTOToRoomConverter());
        converters.add(roomToRoomDTOConverter());
        converters.add(userToUserDtoConverter());
        converters.add(userDtoToUserConverter());
        converters.add(messageToMessageDTOConverter());
        converters.add(messageToMessageNotificationDtoConverter());
        converters.add(messageDTOToMessageConverter());
        converters.add(messageToWordSettingDtoConverter());
        converters.add(userToPlayerConverter());
        converters.add(playerToUserDTOConverter());
        converters.add(playerToPlayerDTOConverter());
        converters.add(questionToQuestionDTOConversion());
        converters.add(playerDtoToPlayerConverter());
        converters.add(userUpdateDtoToUserConverter());
        converters.add(questionToQuestionDtoConverter());
        converters.add(feedbackDtoToFeedbackConverter());
        converters.add(feedbackToFeedbackDtoConverter());
        converters.add(userToFriendConverter());

        bean.setConverters(converters);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(100);
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    @Qualifier("MyMessageSource")
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("classpath:messages");
        return messageSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(HedbanzApiApplication.class, args);
    }
}
