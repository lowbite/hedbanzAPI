package com.hedbanz.hedbanzAPI.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "feedback_text", nullable = false)
    private String feedbackText;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "device_version")
    private Integer deviceVersion;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "device_manufacturer")
    private String deviceManufacturer;

    @Column(name = "product")
    private String product;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdAt;

    public Feedback() {
    }

    public Feedback(String feedbackText, User user, Integer deviceVersion, String deviceName, String deviceModel, String deviceManufacturer, String product, Timestamp createdAt) {
        this.feedbackText = feedbackText;
        this.user = user;
        this.deviceVersion = deviceVersion;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.deviceManufacturer = deviceManufacturer;
        this.product = product;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(Integer deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private String feedbackText;
        private User user;
        private Integer deviceVersion;
        private String deviceName;
        private String deviceModel;
        private String deviceManufacturer;
        private String product;
        private Timestamp createdAt;

        public Builder setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
            return this;
        }

        public Builder setUser(User user) {
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

        public Builder setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Feedback build() {
            return new Feedback(feedbackText, user, deviceVersion, deviceName, deviceModel, deviceManufacturer, product, createdAt);
        }
    }
}
