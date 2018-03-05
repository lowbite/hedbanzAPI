package com.hedbanz.hedbanzAPI.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.hedbanz.hedbanzAPI.entity.UpdateUserData;

import java.io.IOException;

public class UpdateUserDataDeserializer extends JsonDeserializer<UpdateUserData> {
    @Override
    public UpdateUserData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        long id = node.get("id").longValue();
        String newLogin = node.get("newLogin").asText();
        String newPassword = node.get("newPassword").asText();
        String oldPassword = node.get("oldPassword").asText();

        UpdateUserData userData = new UpdateUserData();
        userData.setId(id);
        userData.setNewLogin(newLogin);
        userData.setNewPassword(newPassword);
        userData.setOldPassword(oldPassword);

        return userData;
    }
}
