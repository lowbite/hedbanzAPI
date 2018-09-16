package com.hedbanz.hedbanzAPI.transfer;

public class PlayerGuessingDto {
    private PlayerDto player;
    private Integer attempt;
    private Long questionId;

    public PlayerGuessingDto() {
    }

    public PlayerGuessingDto(PlayerDto player, Integer attempt, Long questionId) {
        this.player = player;
        this.attempt = attempt;
        this.questionId = questionId;
    }

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public static PlayerGuessingDtoBuilder PlayerGuessingDtoBuilder(){
        return new PlayerGuessingDto(). new PlayerGuessingDtoBuilder();
    }

    @Override
    public String toString() {
        return "PlayerGuessingDto{" +
                "player=" + player +
                ", attempt=" + attempt +
                ", questionId=" + questionId +
                '}';
    }

    public class PlayerGuessingDtoBuilder{
        private PlayerGuessingDtoBuilder(){}

        public PlayerGuessingDtoBuilder setPlayer(PlayerDto player){
            PlayerGuessingDto.this.player = player;
            return this;
        }

        public PlayerGuessingDtoBuilder setAttempt(Integer attempt){
            PlayerGuessingDto.this.attempt = attempt;
            return this;
        }

        public PlayerGuessingDtoBuilder setQuestionId(Long questionId){
            PlayerGuessingDto.this.questionId = questionId;
            return this;
        }

        public PlayerGuessingDto build(){
            return PlayerGuessingDto.this;
        }
    }
}
