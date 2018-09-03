package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.deserializer.MessageDeserializer;

import java.util.Date;


public class MessageDto {
    private Long clientMessageId;
    private UserDto senderUser;
    private Long roomId;
    private String text;
    private Integer type;
    private Long createDate;

    public MessageDto() {
    }

    protected MessageDto(Long clientMessageId, UserDto senderUser, Long roomId, String text, Integer type, Date createDate) {
        this.clientMessageId = clientMessageId;
        this.senderUser = senderUser;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        if(createDate != null)
            this.createDate = createDate.getTime();
    }

    public MessageDto(Long senderId, String senderLogin, Integer senderImagePath, Long roomId, String text, MessageType type, Date createDate) {
        this.senderUser = new UserDto.Builder().setId(senderId)
                                                    .setLogin(senderLogin)
                                                    .setIconId(senderImagePath)
                                                    .build();
        this.roomId = roomId;
        this.text = text;
        this.type = type.getCode();
        if(createDate != null)
            this.createDate = createDate.getTime();
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    @JsonSetter("clientMessageId")
    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public UserDto getSenderUser() {
        return senderUser;
    }

    @JsonSetter("senderUser")
    public void setSenderUser(UserDto senderUser) {
        this.senderUser = senderUser;
    }

    public Long getRoomId() {
        return roomId;
    }

    @JsonSetter("roomId")
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getText() {
        return text;
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateDate() {
        return createDate;
    }

    @JsonSetter("createDate")
    public void setCreateDate(Long   createDate) {
        this.createDate = createDate;
    }

    public static class MessageDTOBuilder {
        private Long clientMessageId;
        private UserDto senderUser;
        private Long roomId;
        private String text;
        private Integer type;
        private Date createDate;

        public MessageDTOBuilder setClientMessageId(Long clientMessageId) {
            this.clientMessageId = clientMessageId;
            return this;
        }

        public MessageDTOBuilder setSenderUser(UserDto senderUser) {
            this.senderUser = senderUser;
            return this;
        }

        public MessageDTOBuilder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public MessageDTOBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public MessageDTOBuilder setType(Integer type) {
            this.type = type;
            return this;
        }

        public MessageDTOBuilder setCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }


        public MessageDto createMessageDTO() {
            return new MessageDto(clientMessageId, senderUser, roomId, text, type, createDate);
        }
    }
}
