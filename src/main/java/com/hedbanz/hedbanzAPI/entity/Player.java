package com.hedbanz.hedbanzAPI.entity;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    private Long wordReceiverUserId;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean isWinner;

    @Version
    private long version;

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

    public Long getWordReceiverUserId() {
        return wordReceiverUserId;
    }

    public void setWordReceiverUserId(Long wordReceiverUserId) {
        this.wordReceiverUserId = wordReceiverUserId;
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

        if (!Objects.equals(id, player.id)) return false;
        return Objects.equals(user, player.user);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", attempt=" + attempt +
                ", status=" + status +
                ", login=" + user.getLogin() +
                ", wordReceiverUserId=" + wordReceiverUserId +
                ", isWinner=" + isWinner +
                '}';
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
                .setRoom(room)
                .setAttempts(attempt)
                .setIsWinner(isWinner)
                .setStatus(status)
                .setWord(word)
                .setWordSettingUserId(wordReceiverUserId)
                .build();
    }

    public static PlayerBuilder PlayerBuilder(){
        return new Player(). new PlayerBuilder();
    }

    public class PlayerBuilder {
        private PlayerBuilder(){

        }

        public PlayerBuilder setId(Long id){
            Player.this.setId(id);
            return this;
        }

        public PlayerBuilder setWord(String word){
            Player.this.setWord(word);
            return this;
        }

        public PlayerBuilder setAttempts(Integer attempts){
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
            Player.this.setWordReceiverUserId(userId);
            return this;
        }

        public PlayerBuilder setIsWinner(Boolean isWinner){
            Player.this.setIsWinner(isWinner);
            return this;
        }

        public PlayerBuilder setRoom(Room room){
            Player.this.setRoom(room);
            return this;
        }

        public Player build() {
            return Player.this;
        }
    }
}
