package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.entity.DTO.ClientInfoDTO;

import java.io.IOException;

public class ClientInfoDTODeserializer extends JsonDeserializer<ClientInfoDTO> {
    @Override
    public ClientInfoDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Gson gson = new Gson();
        return gson.fromJson(node.asText(), ClientInfoDTO.class);
    }
}
