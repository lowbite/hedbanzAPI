package com.hedbanz.hedbanzAPI.entity.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.MessageDeserializer;
import com.hedbanz.hedbanzAPI.entity.User;

import java.util.Date;


@JsonDeserialize(using = MessageDeserializer.class)
public class MessageDTO {
    private Long clientMessageId;
    private UserDTO senderUser;
    private Long roomId;
    private String text;
    private Integer type;
    private Long createDate;

    public MessageDTO() {
    }

    public MessageDTO(Long clientMessageId, UserDTO senderUser, Long roomId, String text, Integer type, Date createDate) {
        this.clientMessageId = clientMessageId;
        this.senderUser = senderUser;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        this.createDate = createDate.getTime();
    }

    public MessageDTO(Long senderId, String senderLogin, String senderImagePath, Long roomId, String text, Integer type, Date createDate) {
        this.senderUser = new UserDTO.UserDTOBuilder().setId(senderId)
                                                    .setLogin(senderLogin)
                                                    .setImagePath(senderImagePath)
                                                    .createUserDTO();
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        if(createDate != null)
            this.createDate = createDate.getTime();
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public UserDTO getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(UserDTO senderId) {
        this.senderUser = senderId;
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
