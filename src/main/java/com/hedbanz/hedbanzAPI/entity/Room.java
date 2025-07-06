package com.hedbanz.hedbanzAPI.entity;

import com.hedbanz.hedbanzAPI.constant.GameStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "room")
public class Room implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "max_players", nullable = false)
    @NotNull
    private Integer maxPlayers;

    @Column(name = "current_players_number", nullable = false)
    @NotNull
    private Integer currentPlayersNumber;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "game_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    @OrderBy("id")
    private List<Player> players = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
    private List<Message> messages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "invite",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> invitedUsers = new HashSet<>();

    @Column(name = "admin")
    @NotNull
    private Long roomAdmin;

    @Column(name = "sticker_id", nullable = false)
    @NotNull
    private Long stickerId;

    @Column(name = "icon_id", nullable = false)
    @NotNull
    private Long iconId;

    public Room(){

    }

    public Room(Long id, String name, boolean isPrivate, int maxPlayers, int currentPlayersNumber) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
    }

    private Room(Long id, String name, String password, Integer maxPlayers, Integer currentPlayersNumber, Boolean isPrivate,
                GameStatus gameStatus, List<Player> players, List<Message> messages, Long roomAdmin, Long stickerId, Long iconId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
        this.isPrivate = isPrivate;
        this.gameStatus = gameStatus;
        this.players = players;
        this.messages = messages;
        this.roomAdmin = roomAdmin;
        this.stickerId = stickerId;
        this.iconId = iconId;
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

    public void addPlayer(Player player){
        if(this.players != null && !this.players.contains(player)) {
            player.setRoom(this);
            this.players.add(player);
        }
    }

    public void removePlayer(Player player){
        if(this.players != null && this.players.contains(player)){
            player.setRoom(null);
            this.players.remove(player);
        }
    }

    public void clearPlayers(){
        players.clear();
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

    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }

    public Long getIconId() {
        return iconId;
    }

    public void setIconId(Long iconId) {
        this.iconId = iconId;
    }

    public Set<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(Set<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public void addInvitedUser(User user){
        this.invitedUsers.add(user);
    }

    public void removeInvitedUser(User user){
        this.invitedUsers.remove(user);
    }

    public void clearInvites(){
        invitedUsers = null;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", currentPlayersNumber=" + currentPlayersNumber +
                ", isPrivate=" + isPrivate +
                ", gameStatus=" + gameStatus +
                ", invitedUsers=" + invitedUsers +
                ", roomAdmin=" + roomAdmin +
                ", stickerId=" + stickerId +
                ", iconId=" + iconId +
                '}';
    }

    public static class Builder {
        private Long id;
        private String name;
        private Boolean isPrivate;
        private Integer maxPlayers;
        private Integer currentPlayersNumber;
        private String password;
        private GameStatus gameStatus;
        private List<Player> players = new ArrayList<>();
        private List<Message> messages = new ArrayList<>();
        private Long roomAdmin;
        private Long stickerId;
        private Long iconId;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder setMaxPlayers(Integer maxPlayers) {
            this.maxPlayers = maxPlayers;
            return this;
        }

        public Builder setCurrentPlayersNumber(Integer currentPlayersNumber) {
            this.currentPlayersNumber = currentPlayersNumber;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setGameStatus(GameStatus gameStatus) {
            this.gameStatus = gameStatus;
            return this;
        }

        public Builder setPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder setMessages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Builder setRoomAdmin(Long roomAdmin) {
            this.roomAdmin = roomAdmin;
            return this;
        }

        public Builder setStickerId(Long stickerId){
            this.stickerId = stickerId;
            return this;
        }

        public Builder setIconId(Long iconId){
            this.iconId = iconId;
            return this;
        }

        public Room build() {
            return new Room(id, name, password, maxPlayers,currentPlayersNumber, isPrivate, gameStatus, players,
                    messages, roomAdmin, stickerId, iconId);
        }
    }
}
