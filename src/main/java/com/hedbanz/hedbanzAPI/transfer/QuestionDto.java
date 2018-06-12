package com.hedbanz.hedbanzAPI.transfer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;;
import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.deserializer.QuestionDtoDeserializer;

import java.util.Date;
import java.util.List;

@JsonDeserialize(using = QuestionDtoDeserializer.class)
public class QuestionDto {
    private Long id;
    private Long questionId;
    private Long clientMessageId;
    private Long senderId;
    private Long roomId;
    private String text;
    private Integer type;
    private Long createDate;
    private List<PlayerDto> yesVoters;
    private List<PlayerDto> noVoters;
    private Integer vote;

    private QuestionDto(Long id, Long questionId, Long clientMessageId, Long senderId, Long roomId, String text, Integer type, Long createDate, List<PlayerDto> yesVoters, List<PlayerDto> noVoters, Integer vote) {
        this.id = id;
        this.questionId = questionId;
        this.clientMessageId = clientMessageId;
        this.senderId = senderId;
        this.roomId = roomId;
        this.text = text;
        this.type = type;
        this.createDate = createDate;
        this.yesVoters = yesVoters;
        this.noVoters = noVoters;
        this.vote = vote;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<PlayerDto> getYesVoters() {
        return yesVoters;
    }

    public void setYesVoters(List<PlayerDto> yesVoters) {
        this.yesVoters = yesVoters;
    }

    public List<PlayerDto> getNoVoters() {
        return noVoters;
    }

    public void setNoVoters(List<PlayerDto> noVoters) {
        this.noVoters = noVoters;
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

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
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

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class QuestionDTOBuilder {
        private Long id;
        private Long senderId;
        private Long roomId;
        private List<PlayerDto> yesVoters;
        private List<PlayerDto> noVoters;
        private Integer vote;
        private String text;
        private Integer type;
        private Long createDate;
        private Long clientMessageId;
        private Long questionId;

        public QuestionDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public QuestionDTOBuilder setYesVoters(List<PlayerDto> yesVoters) {
            this.yesVoters = yesVoters;
            return this;
        }

        public QuestionDTOBuilder setNoVoters(List<PlayerDto> noVoters) {
            this.noVoters = noVoters;
            return this;
        }

        public QuestionDTOBuilder setSenderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public QuestionDTOBuilder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public QuestionDTOBuilder setVoteType(Integer vote) {
            this.vote = vote;
            return this;
        }

        public QuestionDTOBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public QuestionDTOBuilder setType(MessageType type) {
            this.type = type.getCode();
            return this;
        }

        public QuestionDTOBuilder setCreateDate(Date createDate) {
            this.createDate = createDate.getTime();
            return this;
        }

        public QuestionDTOBuilder setClientId(Long clientId) {
            this.clientMessageId = clientId;
            return this;
        }

        public QuestionDTOBuilder setQuestionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public QuestionDto build() {
            return new QuestionDto(id, questionId, clientMessageId, senderId, roomId, text, type, createDate, yesVoters, noVoters, vote);
        }
    }

}
