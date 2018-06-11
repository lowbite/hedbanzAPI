package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.deserializer.MessageDeserializer;

import java.util.Date;


@JsonDeserialize(using = MessageDeserializer.class)
public class MessageDto {
    private Long clientMessageId;
    private UserDto senderUser;
    private Long roomId;
    private String text;
    private Integer type;
    private Long createDate;
    private QuestionDto question;

    public MessageDto() {
    }

    private MessageDto(Long clientMessageId, UserDto senderUser, Long roomId, String text, Integer type, Date createDate, QuestionDto question) {
        this.clientMessageId = clientMessageId;
        this.senderUser = senderUser;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        if(createDate != null)
            this.createDate = createDate.getTime();
        this.question = question;
    }

    public MessageDto(Long senderId, String senderLogin, String senderImagePath, Long roomId, String text, MessageType type, Date createDate) {
        this.senderUser = new UserDto.UserDTOBuilder().setId(senderId)
                                                    .setLogin(senderLogin)
                                                    .setImagePath(senderImagePath)
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

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public UserDto getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(UserDto senderId) {
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

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public static class MessageDTOBuilder {
        private Long clientMessageId;
        private UserDto senderUser;
        private Long roomId;
        private String text;
        private Integer type;
        private Date createDate;
        private QuestionDto question;

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

        public MessageDTOBuilder setQuestion(QuestionDto question) {
            this.question = question;
            return this;
        }

        public MessageDto createMessageDTO() {
            return new MessageDto(clientMessageId, senderUser, roomId, text, type, createDate, question);
        }
    }
}
