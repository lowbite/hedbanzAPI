package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.entity.UserToRoom;

import java.io.IOException;

public class UserToRoomDeserializer extends JsonDeserializer<UserToRoom> {

    public UserToRoom deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Gson gson = new Gson();
        UserToRoom userToRoom = gson.fromJson(node.asText(), UserToRoom.class);

        if(userToRoom.getPassword() == null)
            userToRoom.setPassword("");

        return userToRoom;
    }
}
