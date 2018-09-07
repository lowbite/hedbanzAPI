package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "advertise")
public class Advertise implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "delay")
    private Integer delay;

    @Column(name = "type")
    private Integer type;

    public Advertise() {
    }

    public Advertise(Integer delay, Integer type) {
        this.delay = delay;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
