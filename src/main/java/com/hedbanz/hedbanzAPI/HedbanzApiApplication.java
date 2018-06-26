package com.hedbanz.hedbanzAPI;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.converter.*;
import com.hedbanz.hedbanzAPI.exception.SocketExceptionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

@EnableCaching
@SpringBootApplication
public class HedbanzApiApplication {

	@Value("${socketIO.hostname}")
	private String socketIOHostname;

	@Value("${socketIO.port}")
	private Integer socketIOPort;

	@Bean
	public SocketIOServer socketIOServer(){
		Configuration configuration = new Configuration();
		configuration.setHostname(socketIOHostname);

		SocketConfig socketConfig = new SocketConfig();
		socketConfig.setReuseAddress(true);
		configuration.setPingInterval(5000);
		configuration.setPingTimeout(10000);
		configuration.setExceptionListener(new SocketExceptionListener());
		configuration.setSocketConfig(socketConfig);
		configuration.setWorkerThreads(100);

		configuration.setPort(socketIOPort);
		return new SocketIOServer(configuration);
	}

	@Bean
	public RoomToRoomDtoConverter roomDTOToRoomConverter(){
		return new RoomToRoomDtoConverter();
	}

	@Bean
	public RoomDtoToRoomConverter roomToRoomDTOConverter(){
		return new RoomDtoToRoomConverter();
	}

	@Bean
    public UserDtoToUserConverter userToUserDTOConverter(){
	    return new UserDtoToUserConverter();
    }

    @Bean
    public UserToUserDtoConverter userDTOToUserConverter(){
	    return new UserToUserDtoConverter();
    }

    @Bean
	public MessageDtoToMessageConverter messageDTOToMessageConverter(){
		return new MessageDtoToMessageConverter();
	}

	@Bean
	public MessageToMessageNotificationDtoConverter messageToMessageNotificationDtoConverter(){
		return new MessageToMessageNotificationDtoConverter();
	}

	@Bean
	public MessageToMessageDtoConverter messageToMessageDTOConverter(){
		return new MessageToMessageDtoConverter();
	}

	@Bean
	public UserToPlayerConverter userToPlayerConverter(){
		return new UserToPlayerConverter();
	}

	@Bean
	public PlayerToUserDtoConverter playerToUserDTOConverter(){
		return new PlayerToUserDtoConverter();
	}

	@Bean
	public PlayerToPlayerDtoConverter playerToPlayerDTOConverter(){
		return new PlayerToPlayerDtoConverter();
	}

	@Bean
	public PlayerDtoToPlayerConverter playerDtoToPlayerConverter(){
		return new PlayerDtoToPlayerConverter();
	}

	@Bean
	public MessageToQuestionDtoConverter questionToQuestionDTOConversion(){
		return new MessageToQuestionDtoConverter();
	}

	@Bean
	public UserUpdateDtoToUserConverter userUpdateDtoToUserConverter(){
		return new UserUpdateDtoToUserConverter();
	}

	@Bean
	public QuestionToQuestionDtoConverter questionToQuestionDtoConverter(){
		return new QuestionToQuestionDtoConverter();
	}

	@Bean(name = "APIConversionService")
	public ConversionService getConversionService(){
		ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
		Set<Converter> converters = new HashSet<>();

		//add the converter
		converters.add(roomDTOToRoomConverter());
		converters.add(roomToRoomDTOConverter());
		converters.add(userDTOToUserConverter());
		converters.add(userToUserDTOConverter());
		converters.add(messageToMessageDTOConverter());
		converters.add(messageToMessageNotificationDtoConverter());
		converters.add(messageDTOToMessageConverter());
		converters.add(userToPlayerConverter());
		converters.add(playerToUserDTOConverter());
		converters.add(playerToPlayerDTOConverter());
		converters.add(questionToQuestionDTOConversion());
		converters.add(playerDtoToPlayerConverter());
		converters.add(userUpdateDtoToUserConverter());
		converters.add(questionToQuestionDtoConverter());

		bean.setConverters(converters);
		bean.afterPropertiesSet();
		return bean.getObject();
	}

	public static void main(String[] args) {
		SpringApplication.run(HedbanzApiApplication.class, args);
	}
}
