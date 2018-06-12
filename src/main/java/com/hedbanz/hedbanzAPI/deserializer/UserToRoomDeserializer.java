package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.transfer.UserToRoomDto;
import org.apache.http.util.TextUtils;

import java.io.IOException;

public class UserToRoomDeserializer extends JsonDeserializer<UserToRoomDto> {

    public UserToRoomDto deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Gson gson = new Gson();
        String nodeString = node.asText();
        if(TextUtils.isEmpty(nodeString)){
            nodeString = node.toString();
        }
        UserToRoomDto userToRoomDto = gson.fromJson(nodeString, UserToRoomDto.class);

        if(userToRoomDto.getPassword() == null)
            userToRoomDto.setPassword("");

        return userToRoomDto;
    }
}
