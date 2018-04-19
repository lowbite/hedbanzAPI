package com.hedbanz.hedbanzAPI.entity.DTO;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

import javax.validation.constraints.NotNull;

public class SetWordDTO {
    @NotNull
    private Long senderId;
    @NotNull
    private Long wordReceiverId;
    @NotNull
    private String word;

    private CustomError error;

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
}
