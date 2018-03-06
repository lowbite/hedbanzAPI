package com.hedbanz.hedbanzAPI.socket;

public class LoginAvailabilityAnswer {
    private boolean isLoginAvailable;

    public LoginAvailabilityAnswer(){
    }

    public LoginAvailabilityAnswer(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }

    public boolean getIsLoginAvailable(){
        return this.isLoginAvailable;
    }

    public void setLoginAvailable(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }
}
