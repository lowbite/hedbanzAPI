package com.hedbanz.hedbanzAPI.model;

public class MessageNotification {
    private String senderName;
    private String text;
    private String roomName;
    private Long roomId;

    public MessageNotification() {
    }

    private MessageNotification(String senderName, String text, String roomName, Long roomId) {
        this.senderName = senderName;
        this.text = text;
        this.roomName = roomName;
        this.roomId = roomId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public static Builder Builder(){
        return new MessageNotification(). new Builder();
    }

    public class Builder {
        private Builder(){}

        public Builder setSenderName(String senderName){
            MessageNotification.this.setSenderName(senderName);
            return this;
        }

        public Builder setText(String text){
            MessageNotification.this.setText(text);
            return this;
        }

        public Builder setRoomName(String roomName){
            MessageNotification.this.setRoomName(roomName);
            return this;
        }

        public Builder setRoomId(Long roomId){
            MessageNotification.this.setRoomId(roomId);
            return this;
        }

        public MessageNotification build(){
            return MessageNotification.this;
        }
    }
}
