package com.hedbanz.hedbanzAPI.socket;

public class LoginAvailabilityReceiveMessage {
    private String login;

    public LoginAvailabilityReceiveMessage() {
    }

    public LoginAvailabilityReceiveMessage(String login) {
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
