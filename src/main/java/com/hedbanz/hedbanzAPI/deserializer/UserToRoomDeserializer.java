package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.entity.DTO.UserToRoomDTO;

import java.io.IOException;

public class UserToRoomDeserializer extends JsonDeserializer<UserToRoomDTO> {

    public UserToRoomDTO deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Gson gson = new Gson();
        UserToRoomDTO userToRoomDTO = gson.fromJson(node.asText(), UserToRoomDTO.class);

        if(userToRoomDTO.getPassword() == null)
            userToRoomDTO.setPassword("");

        return userToRoomDTO;
    }
}
