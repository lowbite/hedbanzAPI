package com.hedbanz.hedbanzAPI.transfer;

public class InvitePushDto {
    private String senderName;
    private String roomName;
    private Long roomId;

    public InvitePushDto() {
    }

    public InvitePushDto(String senderName, String roomName, Long roomId) {
        this.senderName = senderName;
        this.roomName = roomName;
        this.roomId = roomId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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
