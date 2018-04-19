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

    @Column(name = "attemps")
    private int attemps;

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

    public int getAttemps() {
        return attemps;
    }

    public void setAttemps(int attemps) {
        this.attemps = attemps;
    }

    public static PlayerBuilder newBuilder(){
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

        public PlayerBuilder setAttemps(int attemps){
            Player.this.setAttemps(attemps);
            return this;
        }

        public Player build() {
            return Player.this;
        }
    }
}
