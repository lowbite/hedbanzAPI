package com.hedbanz.hedbanzAPI.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RoomDeserializer extends JsonDeserializer<Room> {
    @Override
    public Room deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        String password = node.get("password").asText();
        int maxPlayers = node.get("maxPlayers").asInt();
        long userId = node.get("userId").asLong();
        Set<User> users = new HashSet<>();
        User user = new User();
        user.setId(userId);
        users.add(user);

        Room room = new Room();
        room.setPassword(password);
        room.setMaxPlayers(maxPlayers);
        room.setUsers(users);
        return room;
    }
}
