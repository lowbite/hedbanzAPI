package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private Set<Player> yesVoters = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private Set<Player> noVoters = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private Set<Player> winVoters = new HashSet<>();

    private Integer attempt;

    @OneToOne(mappedBy = "question")
    private Message message;

    public Question(){}

    private Question(Set<Player> yesVoters, Set<Player> noVoters, Set<Player> winVoters, Integer attempt, Message message) {
        this.yesVoters = yesVoters;
        this.noVoters = noVoters;
        this.winVoters = winVoters;
        this.attempt = attempt;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Player> getYesVoters() {
        return yesVoters;
    }

    public void setYesVoters(Set<Player> yesVoters) {
        this.yesVoters = yesVoters;
    }

    public Set<Player> getNoVoters() {
        return noVoters;
    }

    public void setNoVoters(Set<Player> noVoters) {
        this.noVoters = noVoters;
    }

    public Set<Player> getWinVoters() {
        return winVoters;
    }

    public void setWinVoters(Set<Player> winVoters) {
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

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", yesVoters=" + yesVoters +
                ", noVoters=" + noVoters +
                ", winVoters=" + winVoters +
                ", attempt=" + attempt +
                '}';
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Builder {
        private Set<Player> yesVoters = new HashSet<>();
        private Set<Player> noVoters = new HashSet<>();
        private Set<Player> winVoters = new HashSet<>();
        private Integer attempt;
        private Message message;

        public Builder setYesVoters(Set<Player> yesVoters) {
            this.yesVoters = yesVoters;
            return this;
        }

        public Builder setNoVoters(Set<Player> noVoters) {
            this.noVoters = noVoters;
            return this;
        }

        public Builder setWinVoters(Set<Player> winVoters) {
            this.winVoters = winVoters;
            return this;
        }

        public Builder setAttempt(Integer attempt){
            this.attempt = attempt;
            return this;
        }

        public Builder setMessage(Message message){
            this.message = message;
            return this;
        }

        public Question build() {
            return new Question(yesVoters, noVoters, winVoters, attempt, message);
        }
    }
}
