package com.hedbanz.hedbanzAPI;

import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.error.CustomError;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

//@SpringBootTest
public class HedbanzApiApplicationTests{
    @Test
    public void contextLoads() {
       /* RestTemplate restTemplate = new RestTemplate();
        long time = System.currentTimeMillis();
        String result = restTemplate.postForObject("http://localhost:8085/rooms/join-user", new UserToRoomDto(5L,181L), String.class);
        RequestResponse<RoomDto> requestResponse = ResponseDeserializer.deserialize(result, RoomDto.class);
        System.out.println("execution time: " + (System.currentTimeMillis() - time));

       List<PlayerDto> playerDtos = new ArrayList<>();
        PlayerDto playerDto = new PlayerDto.PlayerDTOBuilder()
                .setUserId(2L)
                .setLogin("Mike")
                .createPlayerDTO();

        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);
        playerDtos.add(playerDto);

        long time = System.nanoTime();
        for (PlayerDto player: playerDtos) {

        }
        System.out.println("execution time: " + (System.nanoTime() - time));

        time = System.nanoTime();
        playerDtos.forEach(player -> {});
        System.out.println("execution time: " + (System.nanoTime() - time));


        String result  = restTemplate.postForObject(String.format("http://localhost:8085/rooms/%d/events/set-word-entity", 151), Arrays.asList(playerDto), String.class);
        RequestResponse<SetWordDto[]> requestResponse = ResponseDeserializer.deserialize(result, SetWordDto[].class);*/
    }

}
class RequestResponse<T> {
    private final String status;
    private final CustomError error;
    private final T data;

    public RequestResponse(String status, CustomError error, T data){
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public CustomError getError() {
        return error;
    }

    public T getData() {
        return data;
    }
}
class ResponseDeserializer {
    public static <T> RequestResponse<T> deserialize(String json, Class<T> tClass){
        Gson gson = new Gson();
        RequestResponse<T> response = gson.fromJson(json, getType(RequestResponse.class, tClass));
        return response;
    }

    private static Type getType(Class<?> rawType, Class<?> parameter){
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{parameter};
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}