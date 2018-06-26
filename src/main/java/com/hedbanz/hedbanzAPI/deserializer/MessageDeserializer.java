package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;

import java.io.IOException;

public class MessageDeserializer extends JsonDeserializer<MessageDto> {

    @Override
    public MessageDto deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException{
        JsonNode node = p.getCodec().readTree(p);
        Gson gson = new Gson();
        return gson.fromJson(node.asText(), MessageDto.class);
    }
}
