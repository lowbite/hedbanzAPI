package com.hedbanz.hedbanzAPI.entity.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.UserToRoomDeserializer;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;

import javax.validation.constraints.NotNull;

@JsonDeserialize(using = UserToRoomDeserializer.class)
public class UserToRoomDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long roomId;
    private String password;
    private CustomError customError;

    public UserToRoomDTO(){

    }

    public UserToRoomDTO(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public UserToRoomDTO(Long userId, Long roomId, String password) {
        this.userId = userId;
        this.roomId = roomId;
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomError getCustomError() {
        return customError;
    }

    public void setCustomError(CustomError customError) {
        this.customError = customError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserToRoomDTO that = (UserToRoomDTO) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (roomId != null ? !roomId.equals(that.roomId) : that.roomId != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
