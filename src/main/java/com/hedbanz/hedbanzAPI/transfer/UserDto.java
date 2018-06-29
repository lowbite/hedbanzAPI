package com.hedbanz.hedbanzAPI.transfer;

import java.sql.Timestamp;

public class UserDto {

    private Long id;
    private String login;
    private String password;
    private Integer money;
    private Timestamp registrationDate;
    private String imagePath;
    private String email;
    private String securityToken;
    private String fcmToken;

    public UserDto(){

    }

    private UserDto(Long id, String login, Integer money, Timestamp registrationDate, String imagePath, String email) {
        this.id = id;
        this.login = login;
        this.money = money;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
        this.email = email;
    }

    public UserDto(Long id, String login, Integer money, Timestamp registrationDate, String imagePath, String email, String securityToken, String fcmToken) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.money = money;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
        this.email = email;
        this.securityToken = securityToken;
        this.fcmToken = fcmToken;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public static class Builder {
        private Long id;
        private String login;
        private Integer money;
        private Timestamp registrationDate;
        private String imagePath;
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

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
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

        public UserDto build() {
            return new UserDto(id, login, money, registrationDate, imagePath, email, token, fcmToken);
        }
    }
}
