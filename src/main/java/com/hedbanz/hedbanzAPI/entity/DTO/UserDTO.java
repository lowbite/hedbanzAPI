package com.hedbanz.hedbanzAPI.entity.DTO;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;

import java.sql.Timestamp;

public class UserDTO {

    private Long id;
    private String login;
    private String password;
    private Integer money;
    private Timestamp registrationDate;
    private String imagePath;
    private String email;
    private String token;
    private CustomError customError;

    public UserDTO(){

    }

    UserDTO(Long id, String login, Integer money, Timestamp registrationDate, String imagePath, String email) {
        this.id = id;
        this.login = login;
        this.money = money;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
        this.email = email;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CustomError getCustomError() {
        return customError;
    }

    public void setCustomError(CustomError customError) {
        this.customError = customError;
    }

    public static class UserDTOBuilder {
        private Long id;
        private String login;
        private Integer money;
        private Timestamp registrationDate;
        private String imagePath;
        private String email;

        public UserDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder setLogin(String login) {
            this.login = login;
            return this;
        }

        public UserDTOBuilder setMoney(Integer money) {
            this.money = money;
            return this;
        }

        public UserDTOBuilder setRegistrationDate(Timestamp registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public UserDTOBuilder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public UserDTOBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDTO createUserDTO() {
            return new UserDTO(id, login, money, registrationDate, imagePath, email);
        }
    }
}
