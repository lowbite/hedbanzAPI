package com.hedbanz.hedbanzAPI.transfer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;;
import com.hedbanz.hedbanzAPI.deserializer.QuestionDtoDeserializer;

import java.util.List;

@JsonDeserialize(using = QuestionDtoDeserializer.class)
public class QuestionDto {
    private Long questionId;
    private Long senderId;
    private Long roomId;
    private List<PlayerDto> yesVoters;
    private List<PlayerDto> noVoters;
    private Integer vote;

    private QuestionDto(Long questionId, Long senderId, Long roomId, List<PlayerDto> yesVoters, List<PlayerDto> noVoters, Integer vote) {
        this.questionId = questionId;
        this.senderId = senderId;
        this.roomId = roomId;
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

    public static class QuestionDTOBuilder {
        private Long id;
        private Long senderId;
        private Long roomId;
        private List<PlayerDto> yesVoters;
        private List<PlayerDto> noVoters;
        private Integer vote;

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

        public QuestionDto build() {
            return new QuestionDto(id, senderId, roomId, yesVoters, noVoters, vote);
        }
    }

}
