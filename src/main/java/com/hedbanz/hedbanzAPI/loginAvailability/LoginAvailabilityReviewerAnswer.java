package com.hedbanz.hedbanzAPI.loginAvailability;

public class LoginAvailabilityReviewerAnswer {
    private boolean isLoginAvailable;

    public LoginAvailabilityReviewerAnswer(){
    }

    public LoginAvailabilityReviewerAnswer(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }

    public boolean getIsLoginAvailable(){
        return this.isLoginAvailable;
    }

    public void setLoginAvailable(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }
}
