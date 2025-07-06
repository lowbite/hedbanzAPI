package com.hedbanz.hedbanzAPI;

import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.springframework.web.client.RestTemplate;

//@SpringBootTest
public class JwtAuthenticationFilterTest {
    public static RestTemplate restTemplate = new RestTemplate();
    public static UserDto userDto = new UserDto();
    public static String USER_URI = "http://localhost:8085/user";
    private final static String BEARER_TOKEN = "Bearer %s";
    private static final String INVALID_TOKEN = "asdasdsa";
    private static final String VALID_TOKEN = "";
   /* @Test
    public void getInvalidJWTTokenError(){
        userDto.setLogin("adasdas");
        userDto.setPassword("asdasdasd");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format(BEARER_TOKEN, INVALID_TOKEN));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(userDto, headers);
        String result  = restTemplate.postForObject(USER_URI, entity, String.class);
        RequestResponse<UserDto> response = ResponseDeserializer.deserialize(result, UserDto.class);
        assertEquals(AuthenticationError.INVALID_JWT_TOKEN.getErrorCode(), response.getError().getErrorCode());
    }

    @Test
    public void getInputErrorWhileAuthentication(){
        userDto.setLogin("asdas");
        userDto.setPassword("sdasd");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(userDto, headers);
        String result  = restTemplate.postForObject(USER_URI, entity, String.class);
        RequestResponse<UserDto> response = ResponseDeserializer.deserialize(result, UserDto.class);
        assertEquals(NotFoundError.NO_SUCH_USER.getErrorCode(), response.getError().getErrorCode());
    }*/
}
