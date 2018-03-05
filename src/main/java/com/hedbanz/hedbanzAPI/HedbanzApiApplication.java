package com.hedbanz.hedbanzAPI;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

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

		configuration.setSocketConfig(socketConfig);

		configuration.setPort(socketIOPort);
		return new SocketIOServer(configuration);
	}

	public static void main(String[] args) {
		SpringApplication.run(HedbanzApiApplication.class, args);
	}


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HedbanzApiApplication.class);
	}
}
