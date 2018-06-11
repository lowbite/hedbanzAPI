package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hedbanz.hedbanzAPI.transfer.UserUpdateDto;

import java.io.IOException;

public class UpdateUserDataDeserializer extends JsonDeserializer<UserUpdateDto> {
    @Override
    public UserUpdateDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        long id = node.get("id").longValue();
        String newLogin = node.get("newLogin").asText();
        String newPassword = node.get("newPassword").asText();
        String oldPassword = node.get("oldPassword").asText();

        UserUpdateDto userData = new UserUpdateDto();
        userData.setId(id);
        userData.setLogin(newLogin);
        userData.setNewPassword(newPassword);
        userData.setOldPassword(oldPassword);

        return userData;
    }
}
