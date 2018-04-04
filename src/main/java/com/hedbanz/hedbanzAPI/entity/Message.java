package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "Message")
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "text")
    private String text;

    @Column(name = "type")
    private Integer type;

    @Column(name = "create_date")
    private Timestamp createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (senderId != null ? !senderId.equals(message.senderId) : message.senderId != null) return false;
        if (roomId != null ? !roomId.equals(message.roomId) : message.roomId != null) return false;
        if (text != null ? !text.equals(message.text) : message.text != null) return false;
        if (type != null ? !type.equals(message.type) : message.type != null) return false;
        return createDate != null ? createDate.equals(message.createDate) : message.createDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
