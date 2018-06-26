package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hedbanz.hedbanzAPI.constant.GameStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

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

    @Column(name = "is_private", columnDefinition = "tinyint(1) default 0", nullable = false)
    private Boolean isPrivate;

    @Column(name = "game_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    @OrderBy("id")
    private List<Player> players = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private List<Message> messages = new ArrayList<>();

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


    public List<Player> getPlayers(){
        return this.players;
    }

    public void setPlayers(List<Player> users) {
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
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

    public boolean containsPlayer(Player player){
        return players.contains(player);
    }

    public boolean addPlayer(Player player){
        if(!this.players.contains(player)) {
            player.setRoom(this);
            this.players.add(player);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player){
        if(this.players.contains(player)){
            player.setRoom(null);
            this.players.remove(player);
            return true;
        }
        return false;
    }

    public Player getPlayerByLogin(String login){
            for(Player player : players){
                if(player.getUser().getLogin().equals(login)){
                    return player;
                }
            }
            return null;
    }

    public int getUserCount(){
        return this.players.size();
    }
}
