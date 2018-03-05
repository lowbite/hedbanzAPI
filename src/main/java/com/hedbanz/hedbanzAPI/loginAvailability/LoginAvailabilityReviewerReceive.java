package com.hedbanz.hedbanzAPI.loginAvailability;

public class LoginAvailabilityReviewerReceive {
    private String login;

    public LoginAvailabilityReviewerReceive() {
    }

    public LoginAvailabilityReviewerReceive(String login) {
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
