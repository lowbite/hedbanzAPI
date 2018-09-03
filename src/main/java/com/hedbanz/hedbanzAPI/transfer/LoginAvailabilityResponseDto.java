package com.hedbanz.hedbanzAPI.transfer;

public class LoginAvailabilityResponseDto {
    private boolean isLoginAvailable;

    public LoginAvailabilityResponseDto(){
    }

    public LoginAvailabilityResponseDto(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }

    public boolean getIsLoginAvailable(){
        return this.isLoginAvailable;
    }

    public void setLoginAvailable(boolean isLoginAvailable){
        this.isLoginAvailable = isLoginAvailable;
    }
}
