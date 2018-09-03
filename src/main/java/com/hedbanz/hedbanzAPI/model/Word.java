package com.hedbanz.hedbanzAPI.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.WordDeserializer;


@JsonDeserialize(using = WordDeserializer.class)
public class Word {
    private Long roomId;
    private Long senderId;
    private String word;
    private Long wordReceiverId;

    public Word() {
    }

    private Word(Long roomId, Long senderId, String word, Long wordReceiverId) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.word = word;
        this.wordReceiverId = wordReceiverId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getWordReceiverId() {
        return wordReceiverId;
    }

    public void setWordReceiverId(Long wordReceiverId) {
        this.wordReceiverId = wordReceiverId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public static class WordDTOBuilder {
        private Long roomId;
        private Long senderId;
        private Long wordReceiverId;
        private String word;

        public WordDTOBuilder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public WordDTOBuilder setSenderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public WordDTOBuilder setWordReceiverId(Long wordReceiverId) {
            this.wordReceiverId = wordReceiverId;
            return this;
        }

        public WordDTOBuilder setWord(String word) {
            this.word = word;
            return this;
        }

        public Word createWordDTO() {
            return new Word(roomId, senderId, word, wordReceiverId);
        }
    }
}
