package com.hedbanz.hedbanzAPI;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.service.RoomService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HedbanzApiApplicationTests {

	@Autowired
	private RoomService roomService;

	@Test
	public void contextLoads() {
	}
	@Test
	public void testMessaging(){
		MessageDTO messageDDTO = new MessageDTO();
		messageDDTO.setSenderId(11);
		messageDDTO.setText("asdasda");
		messageDDTO.setRoomId(156);
		messageDDTO.setType(1);
		roomService.addMessage(messageDDTO);
	}
}
