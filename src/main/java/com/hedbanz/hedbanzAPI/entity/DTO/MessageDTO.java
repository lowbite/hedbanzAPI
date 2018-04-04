package com.hedbanz.hedbanzAPI.entity.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.MessageDeserializer;

import java.util.Date;


@JsonDeserialize(using = MessageDeserializer.class)
public class MessageDTO {
    private Long clientMessageId;
    private Long senderId;
    private Long roomId;
    private String text;
    private Integer type;
    private Long createDate;

    public MessageDTO() {
    }

    public MessageDTO(long clientMessageId, long senderId, long roomId, String text, int type, Date createDate) {
        this.clientMessageId = clientMessageId;
        this.senderId = senderId;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        this.createDate = createDate.getTime();
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long   createDate) {
        this.createDate = createDate;
    }
}
