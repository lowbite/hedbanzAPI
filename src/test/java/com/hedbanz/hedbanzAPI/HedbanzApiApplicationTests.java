package com.hedbanz.hedbanzAPI;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HedbanzApiApplicationTests {

	@Test
	public void contextLoads() {
		A a1 = new A();
		A a2 = new A();
		System.out.println("A1: " + Integer.toHexString(System.identityHashCode(a1)));
		System.out.println("A2: " + Integer.toHexString(System.identityHashCode(a2)));
	}

	class A{
		int i;
	}
}
