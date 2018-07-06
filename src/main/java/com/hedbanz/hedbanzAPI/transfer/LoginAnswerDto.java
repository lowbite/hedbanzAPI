package com.hedbanz.hedbanzAPI.transfer;

public class LoginAnswerDto {
    private boolean isLoginAvailable;

    public LoginAnswerDto(){
    }

    public LoginAnswerDto(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }

    public boolean getIsLoginAvailable(){
        return this.isLoginAvailable;
    }

    public void setLoginAvailable(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }
}
