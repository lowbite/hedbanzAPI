package com.hedbanz.hedbanzAPI.transfer;

public class LoginAvailabilityDto {
    private String login;

    public LoginAvailabilityDto() {
    }

    public LoginAvailabilityDto(String login) {
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
