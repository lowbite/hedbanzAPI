package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "yes_number")
    private Integer yesNumber;

    @Column(name = "no_number")
    private Integer noNumber;

    public Question(){}

    private Question(Integer yesNumber, Integer noNumber) {
        this.yesNumber = yesNumber;
        this.noNumber = noNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (id != null ? !id.equals(question.id) : question.id != null) return false;
        if (yesNumber != null ? !yesNumber.equals(question.yesNumber) : question.yesNumber != null) return false;
        return noNumber != null ? noNumber.equals(question.noNumber) : question.noNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (yesNumber != null ? yesNumber.hashCode() : 0);
        result = 31 * result + (noNumber != null ? noNumber.hashCode() : 0);
        return result;
    }

    public static class QuestionBuilder {
        private Integer yesNumber;
        private Integer noNumber;

        public QuestionBuilder setYesNumber(Integer yesNumber) {
            this.yesNumber = yesNumber;
            return this;
        }

        public QuestionBuilder setNoNumber(Integer noNumber) {
            this.noNumber = noNumber;
            return this;
        }

        public Question createQuestion() {
            return new Question(yesNumber, noNumber);
        }
    }
}
