package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserialize.RoomDeserializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "room")
@JsonDeserialize(using = RoomDeserializer.class)
public class Room implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "max_players")
    private int maxPlayers;

    @OneToMany(orphanRemoval = true)
    @JoinTable(name = "user_room",
                joinColumns = @JoinColumn(name = "room_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> users;

    @Column(name = "current_players_number")
    private int currentPlayersNumber;

    public Room(){

    }

    public Room(long id, String name, String password, int maxPlayers, int currentPlayersNumber) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
