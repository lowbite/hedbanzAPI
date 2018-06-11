package com.hedbanz.hedbanzAPI.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
    private List<Player> yesVoters;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
    private List<Player> noVoters;

    public Question(){}

    private Question(List<Player> yesVoters, List<Player> noVoters) {
        this.yesVoters = yesVoters;
        this.noVoters = noVoters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Player> getYesVoters() {
        return yesVoters;
    }

    public void setYesVoters(List<Player> yesVoters) {
        this.yesVoters = yesVoters;
    }

    public List<Player> getNoVoters() {
        return noVoters;
    }

    public void setNoVoters(List<Player> noVoters) {
        this.noVoters = noVoters;
    }

    public boolean addYesVoter(Player player){
        if(!yesVoters.contains(player)){
            yesVoters.add(player);
            return true;
        }
        return false;
    }

    public boolean removeYesVoter(Player player){
        if(yesVoters.contains(player)){
            yesVoters.remove(player);
            return true;
        }
        return false;
    }

    public boolean addNoVoter(Player player){
        if(!noVoters.contains(player)){
            noVoters.add(player);
            return true;
        }
        return false;
    }


    public boolean removeNoVoter(Player player){
        if(noVoters.contains(player)){
            noVoters.remove(player);
            return true;
        }
        return false;
    }

    public boolean noVotersContainPlayer(Player player){
        return noVoters.contains(player);
    }

    public boolean yesVotersContainPlayer(Player player){
        return yesVoters.contains(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        return id != null ? id.equals(question.id) : question.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class QuestionBuilder {
        private List<Player> yesVoters;
        private List<Player> noVoters;

        public QuestionBuilder setYesVoters(List<Player> yesVoters) {
            this.yesVoters = yesVoters;
            return this;
        }

        public QuestionBuilder setNoVoters(List<Player> noVoters) {
            this.noVoters = noVoters;
            return this;
        }

        public Question createQuestion() {
            return new Question(yesVoters, noVoters);
        }
    }
}
