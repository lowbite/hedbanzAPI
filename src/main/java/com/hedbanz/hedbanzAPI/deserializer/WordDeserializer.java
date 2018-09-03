package com.hedbanz.hedbanzAPI.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.hedbanz.hedbanzAPI.model.Word;

import java.io.IOException;

public class WordDeserializer extends JsonDeserializer<Word> {

    @Override
    public Word deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return new Word.WordDTOBuilder()
                .setWordReceiverId(node.get("wordReceiverId").asLong())
                .setRoomId(node.get("roomId").asLong())
                .setWord(node.get("word").asText())
                .setSenderId(node.get("senderId").asLong())
                .createWordDTO();
    }
}
