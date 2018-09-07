package com.hedbanz.hedbanzAPI.model;

import com.fasterxml.jackson.annotation.JsonSetter;

public class FcmPush {
    private String to;
    private Notification notification;
    private FcmPushData data;
    private String priority;

    public FcmPush() {
    }

    private FcmPush(String to, Notification notification, FcmPushData data, String priority) {
        this.to = to;
        this.notification = notification;
        this.data = data;
        this.priority = priority;
    }

    public String getTo() {
        return to;
    }

    @JsonSetter("to")
    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    @JsonSetter("notification")
    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public FcmPushData getData() {
        return data;
    }

    @JsonSetter("data")
    public void setData(FcmPushData data) {
        this.data = data;
    }

    public String getPriority() {
        return priority;
    }

    @JsonSetter("priority")
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static class Builder {
        private String to;
        private Notification notification;
        private FcmPushData data;
        private String priority;

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public Builder setNotification(Notification notification) {
            this.notification = notification;
            return this;
        }

        public Builder setData(FcmPushData data) {
            this.data = data;
            return this;
        }

        public Builder setPriority(String priority) {
            this.priority = priority;
            return this;
        }

        public FcmPush build() {
            return new FcmPush(to, notification, data, priority);
        }
    }

    public static class FcmPushData<T>{
        private Integer type;
        private T data;

        public FcmPushData() {
        }

        public FcmPushData(Integer type, T data) {
            this.type = type;
            this.data = data;
        }

        public Integer getType() {
            return type;
        }

        @JsonSetter("type")
        public void setType(Integer type) {
            this.type = type;
        }

        public T getData() {
            return data;
        }

        @JsonSetter("data")
        public void setData(T data) {
            this.data = data;
        }
    }
}
