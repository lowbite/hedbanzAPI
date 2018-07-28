package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table
public class PasswordResetKeyWord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    private String keyWord;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private Date expireDate;

    public PasswordResetKeyWord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setExpireDate(int minutes){
        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.MINUTE, minutes);
        this.expireDate = currentTime.getTime();
    }

    public boolean isExpired(){
        return new Date().after(this.expireDate);
    }
}
