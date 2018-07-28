package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.ApplicationDeserializer;

import javax.persistence.*;

@Entity
@Table
@JsonDeserialize(using = ApplicationDeserializer.class)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer appVersion;

    public Application() {
    }

    public Application(Integer id, Integer version) {
        this.id = id;
        this.appVersion = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return appVersion;
    }

    public void setVersion(Integer version) {
        this.appVersion = version;
    }
}
