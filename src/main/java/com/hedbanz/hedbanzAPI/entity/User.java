package com.hedbanz.hedbanzAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {
    public User(){

    }

    public User(Long id, String login, Integer money, Date registrationDate, String imagePath, String email) {
        this.id = id;
        this.login = login;
        this.money = money;
        this.registrationDate = new Timestamp(registrationDate.getTime());
        this.imagePath = imagePath;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login")
    @NotNull
    private String login;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "money")
    @NotNull
    private Integer money;

    @Column(name = "registration_date")
    private Timestamp registrationDate;

    @Column(name = "image_path")
    @NotNull
    private String imagePath;

    @Column(name = "email")
    @NotNull
    private String email;

    @Column(name = "token")
    private String token;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "friendship",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnore
    private Set<User> friends;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public boolean addFriend(User user){
        if(!friends.contains(user)){
            friends.add(user);
            return true;
        }
        return false;
    }

    public boolean removeFriend(User user){
        if(friends.contains(user)){
            friends.remove(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (money != null ? !money.equals(user.money) : user.money != null) return false;
        if (registrationDate != null ? !registrationDate.equals(user.registrationDate) : user.registrationDate != null)
            return false;
        if (imagePath != null ? !imagePath.equals(user.imagePath) : user.imagePath != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return token != null ? token.equals(user.token) : user.token == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
