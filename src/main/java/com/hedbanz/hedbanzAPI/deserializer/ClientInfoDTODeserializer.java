package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.transfer.ClientInfoDto;

import java.io.IOException;

public class ClientInfoDTODeserializer extends JsonDeserializer<ClientInfoDto> {
    @Override
    public ClientInfoDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Gson gson = new Gson();
        return gson.fromJson(node.asText(), ClientInfoDto.class);
    }
}
