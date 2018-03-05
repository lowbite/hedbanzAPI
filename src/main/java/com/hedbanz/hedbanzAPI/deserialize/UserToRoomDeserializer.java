package com.hedbanz.hedbanzAPI.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hedbanz.hedbanzAPI.entity.UserToRoom;

import java.io.IOException;

public class UserToRoomDeserializer extends JsonDeserializer<UserToRoom> {

    public UserToRoom deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        return new UserToRoom();
    }
}
