package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "room")
public class Room implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "name")
    @NotNull
    private String name;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "max_players")
    @NotNull
    private Integer maxPlayers;

    @Column(name = "current_players_number")
    @NotNull
    private Integer currentPlayersNumber;

    @Column(name = "is_private")
    @NotNull
    private Boolean isPrivate;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(name = "user_room",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Room(){

    }

    public Room(Long id, String name, boolean isPrivate, int maxPlayers, int currentPlayersNumber) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
    }


    public Set<User> getUsers(){
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users= users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setMessages(List<Message> messages){
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean addUser(User user){
        if(!this.users.contains(user)){
            this.users.add(user);
            return true;
        }
        return false;
    }

    public boolean removeUser(User user){
        if(this.users.contains(user)){
            this.users.remove(user);
            return true;
        }
        return false;
    }

    public int getUserCount(){
        return this.users.size();
    }

    public void addMessage(Message message){
        messages.size();
        messages.add(message);
    }
}
