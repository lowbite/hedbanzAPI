package com.hedbanz.hedbanzAPI.entity;


public final class Vote{
    private Long senderId;
    private Long roomId;
    private Long questionId;
    private Integer voteType;

    public Vote() {
    }

    private Vote(Long senderId, Long roomId, Long questionId, Integer voteType) {
        this.senderId = senderId;
        this.roomId = roomId;
        this.questionId = questionId;
        this.voteType = voteType;
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getVoteType() {
        return voteType;
    }

    public void setVoteType(Integer voteType) {
        this.voteType = voteType;
    }

    public static VoteBuilder VoteBuilder(){
        return new Vote().new VoteBuilder();
    }

    public class VoteBuilder{
        private VoteBuilder() {
        }

        public Vote.VoteBuilder setSenderId(Long senderId){
            Vote.this.setSenderId(senderId);
            return this;
        }

        public Vote.VoteBuilder setRoomId(Long roomId){
            Vote.this.setRoomId(roomId);
            return this;
        }

        public Vote.VoteBuilder setQuestionId(Long questionId){
            Vote.this.setQuestionId(questionId);
            return this;
        }

        public Vote.VoteBuilder setVoteType(Integer voteType){
            Vote.this.setVoteType(voteType);
            return this;
        }

        public Vote build(){
            return Vote.this;
        }
    }
}
