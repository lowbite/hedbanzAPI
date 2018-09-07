package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class InviteDto {
    private Long senderId;
    private Long roomId;
    private List<Long> invitedUserIds;

    public InviteDto() {
    }

    public InviteDto(Long senderId, Long roomId, List<Long> invitedUserIds) {
        this.senderId = senderId;
        this.roomId = roomId;
        this.invitedUserIds = invitedUserIds;
    }

    public Long getSenderId() {
        return senderId;
    }

    @JsonSetter("senderId")
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRoomId() {
        return roomId;
    }

    @JsonSetter("roomId")
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public List<Long> getInvitedUserIds() {
        return invitedUserIds;
    }

    @JsonSetter("invitedUserIds")
    public void setInvitedUserIds(List<Long> invitedUserIds) {
        this.invitedUserIds = invitedUserIds;
    }
}
