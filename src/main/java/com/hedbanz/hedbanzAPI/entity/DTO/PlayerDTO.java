package com.hedbanz.hedbanzAPI.entity.DTO;

public class PlayerDTO {
    private Long id;
    private String login;
    private String imagePath;
    private String word;
    private Integer attempts;
    private Boolean isAFK;
    private Boolean isFriend;

    public PlayerDTO(){}

    public PlayerDTO(Long id, String login, String imagePath, String word, Integer attempts, Boolean isAFK, Boolean isFriend){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.word = word;
        this.attempts = attempts;
        this.isAFK = isAFK;
        this.isFriend = isFriend;
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

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean friend) {
        isFriend = friend;
    }

    public static class PlayerDTOBuilder {
        private Long id;
        private String login;
        private String imagePath;
        private String word;
        private Integer attempts;
        private Boolean isAFK;
        private Boolean isFriend;

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

        public PlayerDTOBuilder setIsAFK(Boolean isAFK) {
            this.isAFK = isAFK;
            return this;
        }

        public PlayerDTOBuilder setIsFriend(Boolean isFriend) {
            this.isFriend = isFriend;
            return this;
        }

        public PlayerDTO createPlayerDTO() {
            return new PlayerDTO(id, login, imagePath, word, attempts, isAFK, isFriend);
        }
    }
}
