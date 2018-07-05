package com.hedbanz.hedbanzAPI.model;

public class AfkWarning {
    private String roomName;
    private Long roomId;

    public AfkWarning() {
    }

    public AfkWarning(String roomName, Long roomId) {
        this.roomName = roomName;
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
