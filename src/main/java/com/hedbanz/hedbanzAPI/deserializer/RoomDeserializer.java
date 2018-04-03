package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
        Set<UserDTO> userDTOS = new HashSet<>();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTOS.add(userDTO);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName(name);
        roomDTO.setPassword(password);
        roomDTO.setMaxPlayers(maxPlayers);
        roomDTO.setIsPrivate(isPrivate);
        roomDTO.setUsers(userDTOS);
        return roomDTO;
    }
}
