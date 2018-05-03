package com.hedbanz.hedbanzAPI.entity.DTO;

public class QuestionDTO {
    private Long questionId;
    private Integer yesNumber;
    private Integer noNumber;

    private QuestionDTO(Long questionId, Integer yesNumber, Integer noNumber) {
        this.questionId = questionId;
        this.yesNumber = yesNumber;
        this.noNumber = noNumber;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getYesNumber() {
        return yesNumber;
    }

    public void setYesNumber(Integer yesNumber) {
        this.yesNumber = yesNumber;
    }

    public Integer getNoNumber() {
        return noNumber;
    }

    public void setNoNumber(Integer noNumber) {
        this.noNumber = noNumber;
    }

    public static class QuestionDTOBuilder {
        private Long id;
        private Integer yesNumber;
        private Integer noNumber;

        public QuestionDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public QuestionDTOBuilder setYesNumber(Integer yesNumber) {
            this.yesNumber = yesNumber;
            return this;
        }

        public QuestionDTOBuilder setNoNumber(Integer noNumber) {
            this.noNumber = noNumber;
            return this;
        }

        public QuestionDTO createQuestionDTO() {
            return new QuestionDTO(id, yesNumber, noNumber);
        }
    }

}
