package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    private List<Player> yesVoters;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    private List<Player> noVoters;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    private List<Player> winVoters;

    private Integer attempt;

    public Question(){}

    private Question(List<Player> yesVoters, List<Player> noVoters, List<Player> winVoters, Integer attempt) {
        this.yesVoters = yesVoters;
        this.noVoters = noVoters;
        this.winVoters = winVoters;
        this.attempt = attempt;
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

    public List<Player> getWinVoters() {
        return winVoters;
    }

    public void setWinVoters(List<Player> winVoters) {
        this.winVoters = winVoters;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public boolean addYesVoter(Player player){
        if(!yesVoters.contains(player)){
            yesVoters.add(player);
            return true;
        }
        return false;
    }

    public void removeYesVoter(Player player){
        if(yesVoters.contains(player))
            yesVoters.remove(player);
    }

    public boolean addNoVoter(Player player) {
        if (!noVoters.contains(player)) {
            noVoters.add(player);
            return true;
        }
        return false;
    }

    public void removeNoVoter(Player player){
        if(noVoters.contains(player))
            noVoters.remove(player);
    }

    public boolean addWinVoter(Player player) {
        if (!winVoters.contains(player)) {
            winVoters.add(player);
            return true;
        }
        return false;
    }

    public void removeWinVoter(Player player){
        if(winVoters.contains(player))
            winVoters.remove(player);
    }

    public boolean noVotersContainPlayer(Player player){
        return noVoters.contains(player);
    }

    public boolean yesVotersContainPlayer(Player player){
        return yesVoters.contains(player);
    }

    public boolean winVotersContainPlayer(Player player){
        return winVoters.contains(player);
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

    public static class Builder {
        private List<Player> yesVoters;
        private List<Player> noVoters;
        private List<Player> winVoters;
        private Integer attempt;

        public Builder setYesVoters(List<Player> yesVoters) {
            this.yesVoters = yesVoters;
            return this;
        }

        public Builder setNoVoters(List<Player> noVoters) {
            this.noVoters = noVoters;
            return this;
        }

        public Builder setWinVoters(List<Player> winVoters) {
            this.winVoters = winVoters;
            return this;
        }

        public Builder setAttempt(Integer attempt){
            this.attempt = attempt;
            return this;
        }

        public Question build() {
            return new Question(yesVoters, noVoters, winVoters, attempt);
        }
    }
}
