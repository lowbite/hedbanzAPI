package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.sql.Timestamp;

public class UserDto {

    private Long id;
    private String login;
    private String password;
    private Integer money;
    private Timestamp registrationDate;
    private Long gamesNumber;
    private Long friendsNumber;
    private Integer iconId;
    private String email;
    private String securityToken;
    private String fcmToken;

    public UserDto(){

    }

    private UserDto(Long id, String login, Integer money, Timestamp registrationDate, Integer iconId, String email) {
        this.id = id;
        this.login = login;
        this.money = money;
        this.registrationDate = registrationDate;
        this.iconId = iconId;
        this.email = email;
    }

    public UserDto(Long id, String login, Integer money, Timestamp registrationDate, Long gamesNumber,
                   Long friendsNumber, Integer iconId, String email,
                   String securityToken, String fcmToken) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.money = money;
        this.registrationDate = registrationDate;
        this.iconId = iconId;
        this.email = email;
        this.securityToken = securityToken;
        this.fcmToken = fcmToken;
        this.gamesNumber = gamesNumber;
        this.friendsNumber = friendsNumber;
    }

    public Long getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    @JsonSetter("login")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    @JsonSetter("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMoney() {
        return money;
    }

    @JsonSetter("money")
    public void setMoney(Integer money) {
        this.money = money;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    @JsonSetter("registrationDate")
    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getIconId() {
        return iconId;
    }

    @JsonSetter("iconId")
    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public String getEmail() {
        return email;
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    @JsonSetter("securityToken")
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    @JsonSetter("fcmToken")
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Long getGamesNumber() {
        return gamesNumber;
    }

    @JsonSetter("gamesNumber")
    public void setGamesNumber(Long gamesNumber) {
        this.gamesNumber = gamesNumber;
    }

    public Long getFriendsNumber() {
        return friendsNumber;
    }

    @JsonSetter("friendsNumber")
    public void setFriendsNumber(Long friendsNumber) {
        this.friendsNumber = friendsNumber;
    }

    public static class Builder {
        private Long id;
        private String login;
        private Integer money;
        private Timestamp registrationDate;
        private Long gamesNumber;
        private Long friendsNumber;
        private Integer iconId;
        private String email;
        private String token;
        private String fcmToken;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setMoney(Integer money) {
            this.money = money;
            return this;
        }

        public Builder setRegistrationDate(Timestamp registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder setIconId(Integer iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }


        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setFcmToken(String fcmToken) {
            this.fcmToken = fcmToken;
            return this;
        }

        public Builder setGamesNumber(Long gamesNumber){
            this.gamesNumber = gamesNumber;
            return this;
        }

        public Builder setFriendsNumber(Long friendsNumber){
            this.friendsNumber = friendsNumber;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, login, money, registrationDate, gamesNumber, friendsNumber, iconId,  email, token, fcmToken);
        }
    }
}
