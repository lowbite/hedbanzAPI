package com.hedbanz.hedbanzAPI.transfer;

import com.hedbanz.hedbanzAPI.constant.PlayerStatus;

public class PlayerDto {
    private Long id;
    private String login;
    private String imagePath;
    private String word;
    private Integer attempts;
    private Integer status;
    private Boolean isFriend;
    private Long userId;

    public PlayerDto(){}

    public PlayerDto(Long id, String login, String imagePath, String word, Integer attempts, PlayerStatus status){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.word = word;
        this.attempts = attempts;
        this.status = status.getCode();
    }

    private PlayerDto(Long id, String login, String imagePath, String word, Integer attempts, Integer status, Boolean isFriend, Long userId){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.word = word;
        this.attempts = attempts;
        this.status = status;
        this.isFriend = isFriend;
        this.userId = userId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer AFK) {
        status = AFK;
    }

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean friend) {
        isFriend = friend;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static class PlayerDTOBuilder {
        private Long id;
        private String login;
        private String imagePath;
        private String word;
        private Integer attempts;
        private Integer status;
        private Boolean isFriend;
        private Long userId;

        public PlayerDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public PlayerDTOBuilder setLogin(String login) {
            this.login = login;
            return this;
        }

        public PlayerDTOBuilder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public PlayerDTOBuilder setWord(String word) {
            this.word = word;
            return this;
        }

        public PlayerDTOBuilder setAttempts(Integer attempts) {
            this.attempts = attempts;
            return this;
        }

        public PlayerDTOBuilder setStatus(Integer status) {
            this.status = status;
            return this;
        }

        public PlayerDTOBuilder setIsFriend(Boolean isFriend) {
            this.isFriend = isFriend;
            return this;
        }

        public PlayerDTOBuilder setUserId(Long userId){
            this.userId = userId;
            return this;
        }

        public PlayerDto createPlayerDTO() {
            return new PlayerDto(id, login, imagePath, word, attempts, status, isFriend, userId);
        }
    }
}
