package com.hedbanz.hedbanzAPI;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HedbanzApiApplicationTests {

	@Test
	public void contextLoads() {
		String arr[] = {"A","B","C","D"};
		for (int i=0;i<arr.length;i++) {
			countAllCombinations(arr[i], i, arr);
		}
	}

	private void countAllCombinations (String input,int idx, String[] options) {
		for(int i = idx ; i < options.length; i++) {
			String output = input + "_" + options[i];
			System.out.println(output);
			countAllCombinations(output,++idx, options);
		}
	}

}
