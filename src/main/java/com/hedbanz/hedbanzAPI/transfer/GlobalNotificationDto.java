package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.annotation.JsonSetter;

public class GlobalNotificationDto {
    private String text;

    public String getText() {
        return text;
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }
}
