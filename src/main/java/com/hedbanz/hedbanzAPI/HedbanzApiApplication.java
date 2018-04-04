package com.hedbanz.hedbanzAPI;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.hedbanz.hedbanzAPI.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

@EnableCaching
@SpringBootApplication
public class HedbanzApiApplication extends SpringBootServletInitializer {

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
		configuration.setPingTimeout(1000);

		configuration.setSocketConfig(socketConfig);

		configuration.setPort(socketIOPort);
		return new SocketIOServer(configuration);
	}

	@Bean
	public RoomToRoomDTOConverter roomDTOToRoomConverter(){
		return new RoomToRoomDTOConverter();
	}

	@Bean
	public RoomDTOToRoomConverter roomToRoomDTOConverter(){
		return new RoomDTOToRoomConverter();
	}

	@Bean
    public UserDTOToUserConverter userToUserDTOConverter(){
	    return new UserDTOToUserConverter();
    }

    @Bean
    public UserToUserDTOConverter userDTOToUserConverter(){
	    return new UserToUserDTOConverter();
    }

    @Bean
	public MessageDTOToMessageConverter messageDTOToMessageConverter(){
		return new MessageDTOToMessageConverter();
	}

	@Bean
	public MessageToMessageDTOConverter messageToMessageDTOConverter(){
		return new MessageToMessageDTOConverter();
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
		converters.add(messageDTOToMessageConverter());

		bean.setConverters(converters);
		bean.afterPropertiesSet();
		return bean.getObject();
	}

	public static void main(String[] args) {
		SpringApplication.run(HedbanzApiApplication.class, args);
	}


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HedbanzApiApplication.class);
	}
}
