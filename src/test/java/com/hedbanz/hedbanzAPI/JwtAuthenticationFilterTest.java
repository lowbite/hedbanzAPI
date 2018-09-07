package com.hedbanz.hedbanzAPI;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.AuthenticationError;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.transfer.UserDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@SpringBootTest
public class JwtAuthenticationFilterTest {
    public static RestTemplate restTemplate = new RestTemplate();
    public static UserDto userDto = new UserDto();
    public static String USER_URI = "http://localhost:8085/user";
    private final static String BEARER_TOKEN = "Bearer %s";
    private static String INVALID_TOKEN = "asdasdsa";
    private static String VALID_TOKEN = "";
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
