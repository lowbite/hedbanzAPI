package com.hedbanz.hedbanzAPI.entity.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.MessageDeserializer;

import java.sql.Timestamp;
import java.util.Date;


@JsonDeserialize(using = MessageDeserializer.class)
public class MessageDTO {
    private long id;
    private long senderId;
    private long roomId;
    private String text;
    private int type;
    private Timestamp createDate;

    public MessageDTO() {
    }

    public MessageDTO(long id, long senderId, long roomId, String text, int type, Date createDate) {
        this.id = id;
        this.senderId = senderId;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        this.createDate = new Timestamp(createDate.getTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
