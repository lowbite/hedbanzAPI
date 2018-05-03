package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Column(name = "player_id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "word")
    private String word;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "is_afk")
    private Boolean isAFK;

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Boolean getIsAFK() {
        return isAFK;
    }

    public void setIsAFK(Boolean AFK) {
        isAFK = AFK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null) return false;
        if (login != null ? !login.equals(player.login) : player.login != null) return false;
        return imagePath != null ? imagePath.equals(player.imagePath) : player.imagePath == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        return result;
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

        public PlayerBuilder setLogin(String login){
            Player.this.setLogin(login);
            return this;
        }

        public PlayerBuilder setImagePath(String imagePath){
            Player.this.setImagePath(imagePath);
            return this;
        }

        public PlayerBuilder setWord(String word){
            Player.this.setWord(word);
            return this;
        }

        public PlayerBuilder setAttempts(int attempts){
            Player.this.setAttempts(attempts);
            return this;
        }

        public PlayerBuilder setIsAFK(Boolean isAFK){
            Player.this.setIsAFK(isAFK);
            return this;
        }

        public Player build() {
            return Player.this;
        }
    }
}
