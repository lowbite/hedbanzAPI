package com.hedbanz.hedbanzAPI.entity.DTO;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

import javax.validation.constraints.NotNull;

public class WordDTO {
    private Long roomId;
    private Long senderId;
    private Long wordReceiverId;
    private String word;
    private CustomError error;

    private WordDTO(Long roomId, Long senderId, Long wordReceiverId, String word) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.wordReceiverId = wordReceiverId;
        this.word = word;
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

    public CustomError getError() {
        return error;
    }

    public void setError(CustomError error) {
        this.error = error;
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

        public WordDTO createWordDTO() {
            return new WordDTO(roomId, senderId, wordReceiverId, word);
        }
    }
}
