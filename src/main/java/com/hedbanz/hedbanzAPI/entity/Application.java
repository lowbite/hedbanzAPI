package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;

@Entity
@Table
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer version;

    public Application() {
    }

    public Application(Integer id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
