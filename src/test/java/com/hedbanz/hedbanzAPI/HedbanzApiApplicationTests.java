package com.hedbanz.hedbanzAPI;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HedbanzApiApplicationTests {

	@Test
	public void contextLoads() {
		outer:
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				System.out.println("Hello");
				break outer;
			}
			System.out.println("outer");
		}
		System.out.println("Good-Bye");
	}
}
