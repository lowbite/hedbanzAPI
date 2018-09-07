package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.annotation.JsonSetter;

public class FeedbackDto {
    private String feedbackText;
    private UserDto user;
    private Integer deviceVersion;
    private String deviceName;
    private String deviceModel;
    private String deviceManufacturer;
    private String product;
    private String createdAt;

    public FeedbackDto() {
    }

    public FeedbackDto(String feedbackText, UserDto user, Integer deviceVersion, String deviceName, String deviceModel, String deviceManufacturer, String product, String createdAt) {
        this.feedbackText = feedbackText;
        this.user = user;
        this.deviceVersion = deviceVersion;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.deviceManufacturer = deviceManufacturer;
        this.product = product;
        this.createdAt = createdAt;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    @JsonSetter("feedbackText")
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public UserDto getUser() {
        return user;
    }

    @JsonSetter("user")
    public void setUser(UserDto user) {
        this.user = user;
    }

    public Integer getDeviceVersion() {
        return deviceVersion;
    }

    @JsonSetter("deviceVersion")
    public void setDeviceVersion(Integer deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @JsonSetter("deviceName")
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    @JsonSetter("deviceModel")
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    @JsonSetter("deviceManufacturer")
    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getProduct() {
        return product;
    }

    @JsonSetter("product")
    public void setProduct(String product) {
        this.product = product;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private String feedbackText;
        private UserDto user;
        private Integer deviceVersion;
        private String deviceName;
        private String deviceModel;
        private String deviceManufacturer;
        private String product;
        private String createdAt;

        public Builder setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
            return this;
        }

        public Builder setUser(UserDto user) {
            this.user = user;
            return this;
        }

        public Builder setDeviceVersion(Integer deviceVersion) {
            this.deviceVersion = deviceVersion;
            return this;
        }

        public Builder setDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
            return this;
        }

        public Builder setDeviceManufacturer(String deviceManufacturer) {
            this.deviceManufacturer = deviceManufacturer;
            return this;
        }

        public Builder setProduct(String product) {
            this.product = product;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public FeedbackDto build() {
            return new FeedbackDto(feedbackText, user, deviceVersion, deviceName, deviceModel, deviceManufacturer, product, createdAt);
        }
    }
}
