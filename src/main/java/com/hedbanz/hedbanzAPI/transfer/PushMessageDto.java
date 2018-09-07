package com.hedbanz.hedbanzAPI.transfer;

public class PushMessageDto {
    private String senderName;
    private String roomName;
    private String text;
    private Long roomId;

    public PushMessageDto() {
    }

    public PushMessageDto(String senderName, String roomName, String text, Long roomId) {
        this.senderName = senderName;
        this.roomName = roomName;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public static class Builder {
        private String senderName;
        private String roomName;
        private String text;
        private Long roomId;

        public Builder setSenderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder setRoomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public PushMessageDto build() {
            return new PushMessageDto(senderName, roomName, text, roomId);
        }
    }
}
