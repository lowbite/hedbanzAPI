package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.UserToRoomDeserializer;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonDeserialize(using = UserToRoomDeserializer.class)
public class UserToRoomDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long roomId;
    private String password;

    public UserToRoomDto(){

    }

    public UserToRoomDto(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public UserToRoomDto(Long userId, Long roomId, String password) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserToRoomDto that = (UserToRoomDto) o;

        if (!Objects.equals(userId, that.userId)) return false;
        if (!Objects.equals(roomId, that.roomId)) return false;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private Long userId;
        private Long roomId;
        private String password;

        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserToRoomDto build() {
            return new UserToRoomDto(userId, roomId, password);
        }
    }
}
