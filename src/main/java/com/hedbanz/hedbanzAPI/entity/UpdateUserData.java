package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.UpdateUserDataDeserializer;

import java.io.Serializable;

@JsonDeserialize(using = UpdateUserDataDeserializer.class)
public class UpdateUserData implements Serializable{

    public UpdateUserData(){

    }

    private Long id;

    private String newLogin;

    private String newPassword;

    private String oldPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
