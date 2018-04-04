package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
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
    private Set<User> users = new HashSet<>();

    @Column(name = "admin")
    @NotNull
    private Long roomAdmin;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

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

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public void setCurrentPlayersNumber(Integer currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setMessages(Set<Message> messages){
        this.messages = messages;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Long getRoomAdmin() {
        return roomAdmin;
    }

    public void setRoomAdmin(Long roomAdmin) {
        this.roomAdmin = roomAdmin;
    }

    public boolean addUser(User user){
        if(!this.users.contains(user)) {
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