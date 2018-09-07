package com.hedbanz.hedbanzAPI.model;

import com.fasterxml.jackson.annotation.JsonSetter;

public class Notification {
    private String title;
    private String body;

    public Notification() {
    }

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    @JsonSetter("body")
    public void setBody(String body) {
        this.body = body;
    }
}
