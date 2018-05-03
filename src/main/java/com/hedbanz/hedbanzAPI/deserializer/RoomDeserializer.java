package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hedbanz.hedbanzAPI.entity.DTO.PlayerDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import com.hedbanz.hedbanzAPI.entity.Player;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomDeserializer extends JsonDeserializer<RoomDTO> {
    @Override
    public RoomDTO deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException{
        JsonNode node = p.getCodec().readTree(p);
        String name = node.get("name").asText();
        String password = node.get("password").asText();
        int maxPlayers = node.get("maxPlayers").asInt();
        long userId = node.get("userId").asLong();
        boolean isPrivate;
        if(TextUtils.isEmpty(password))
            isPrivate = false;
        else
            isPrivate = true;
        List<PlayerDTO> playerDTOS = new ArrayList<>();
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(userId);
        playerDTOS.add(playerDTO);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName(name);
        roomDTO.setPassword(password);
        roomDTO.setMaxPlayers(maxPlayers);
        roomDTO.setIsPrivate(isPrivate);
        roomDTO.setPlayers(playerDTOS);
        return roomDTO;
    }
}
