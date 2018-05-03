package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
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

    @Column(name = "is_game_started", columnDefinition = "default 'false'")
    private Boolean isGameStarted;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Player> players = new HashSet<>();

    @Column(name = "admin")
    @NotNull
    private Long roomAdmin;

    public Room(){

    }

    public Room(Long id, String name, boolean isPrivate, int maxPlayers, int currentPlayersNumber) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
    }


    public Set<Player> getPlayers(){
        return this.players;
    }

    public void setPlayers(Set<Player> users) {
        this.players = users;
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

    public Long getRoomAdmin() {
        return roomAdmin;
    }

    public void setRoomAdmin(Long roomAdmin) {
        this.roomAdmin = roomAdmin;
    }

    public boolean updatePlayer(Player player){
        if(this.players.contains(player)){
            for (Player roomPlayer : players) {
                if (roomPlayer.getId().equals(player.getId())) {
                    roomPlayer = player;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isContainPlayer(Player player){
        return players.contains(player);
    }

    public boolean addPlayer(Player player){
        if(!this.players.contains(player)) {
            this.players.add(player);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player){
        if(this.players.contains(player)){
            this.players.remove(player);
            return true;
        }
        return false;
    }

    public int getUserCount(){
        return this.players.size();
    }

    public Boolean getIsGameStarted() {
        return isGameStarted;
    }

    public void setIsGameStarted(Boolean gameStarted) {
        isGameStarted = gameStarted;
    }
}
