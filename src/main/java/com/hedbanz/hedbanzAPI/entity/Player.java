package com.hedbanz.hedbanzAPI.entity;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "players")
public class Player implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "player_id")
    private Long id;

    @Column(name = "word")
    private String word;

    @Column(name = "attempt", columnDefinition = "int(11) default 0")
    private Integer attempt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PlayerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Long wordSettingUserId;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean isWinner;

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus AFK) {
        status = AFK;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getWordSettingUserId() {
        return wordSettingUserId;
    }

    public void setWordSettingUserId(Long wordSettingUserId) {
        this.wordSettingUserId = wordSettingUserId;
    }

    public boolean getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(boolean winner) {
        isWinner = winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null) return false;
        return user != null ? user.getId().equals(player.user.getId()) : player.user == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone(){
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return Player.PlayerBuilder()
                .setId(id)
                .setUser(user)
                .setAttempts(attempt)
                .setIsWinner(isWinner)
                .setStatus(status)
                .setWord(word)
                .setWordSettingUserId(wordSettingUserId)
                .build();
    }

    public static PlayerBuilder PlayerBuilder(){
        return new Player(). new PlayerBuilder();
    }

    public class PlayerBuilder {
        private PlayerBuilder(){

        }

        public PlayerBuilder setId(long id){
            Player.this.setId(id);
            return this;
        }

        public PlayerBuilder setWord(String word){
            Player.this.setWord(word);
            return this;
        }

        public PlayerBuilder setAttempts(int attempts){
            Player.this.setAttempt(attempts);
            return this;
        }

        public PlayerBuilder setStatus(PlayerStatus status){
            Player.this.setStatus(status);
            return this;
        }

        public PlayerBuilder setUser(User user){
            Player.this.setUser(user);
            return this;
        }

        public PlayerBuilder setWordSettingUserId(Long userId){
            Player.this.setWordSettingUserId(userId);
            return this;
        }

        public PlayerBuilder setIsWinner(Boolean isWinner){
            Player.this.setIsWinner(isWinner);
            return this;
        }

        public Player build() {
            return Player.this;
        }
    }
}
