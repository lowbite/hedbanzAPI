package com.hedbanz.hedbanzAPI.transfer;

import java.util.List;

public class InviteDto {
    private Long senderId;
    private Long roomId;
    private List<Long> invitedUserId;
    private String password;

    public InviteDto(Long senderId, Long roomId, List<Long> invitedUserId, String password) {
        this.senderId = senderId;
        this.roomId = roomId;
        this.invitedUserId = invitedUserId;
        this.password = password;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public List<Long> getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(List<Long> invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
