package com.hedbanz.hedbanzAPI.transfer;

public class LoginDto {
    private String login;

    public LoginDto() {
    }

    public LoginDto(String login) {
        super();
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
}
