package com.hedbanz.hedbanzAPI.transfer;

public class SetWordNotification {
    private Long roomId;
    private String roomName;

    public SetWordNotification(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
