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

    public FeedbackDto() {
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

    @JsonSetter("feedbackText")
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
}
