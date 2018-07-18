package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

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

    @Column(name = "securityToken")
    private String securityToken;

    @Column(name = "fcm_token")
    private String fcmToken;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "friendship",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friends;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "invite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    private List<Room> invitedToRooms;

    public User() {

    }

    public User(Long userId, String login, Integer money, Date registrationDate, String imagePath, String email) {
        this.userId = userId;
        this.login = login;
        this.money = money;
        this.registrationDate = new Timestamp(registrationDate.getTime());
        this.imagePath = imagePath;
        this.email = email;
    }

    public User(Long userId, String login, String password, Integer money, Timestamp registrationDate, String imagePath,
                String email, String securityToken, String fcmToken, List<User> friends, List<Room> invitedToRooms) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.money = money;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
        this.email = email;
        this.securityToken = securityToken;
        this.fcmToken = fcmToken;
        this.friends = friends;
        this.invitedToRooms = invitedToRooms;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public List<User> getFriends() {
        return friends;
    }

    public List<Room> getInvitedToRooms() {
        return invitedToRooms;
    }

    public boolean addFriend(User user) {
        if (!friends.contains(user)) {
            friends.add(user);
            return true;
        }
        return false;
    }

    public boolean removeFriend(User user) {
        if (friends.contains(user)) {
            friends.remove(user);
            return true;
        }
        return false;
    }

    public boolean addInvite(Room room) {
        if (!invitedToRooms.contains(room)) {
            invitedToRooms.add(room);
            return true;
        }
        return false;
    }

    public boolean removeInvite(Room room) {
        if (invitedToRooms.contains(room)) {
            invitedToRooms.remove(room);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != null ? !userId.equals(user.userId) : user.userId != null) return false;
        return login != null ? login.equals(user.login) : user.login == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

    public static Builder Builder() {
        return new User().new Builder();
    }

    public class Builder {
        private Builder() {

        }

        public Builder setUserId(Long id) {
            User.this.setUserId(id);
            return this;
        }

        public Builder setLogin(String login) {
            User.this.setLogin(login);
            return this;
        }

        public Builder setPassword(String password) {
            User.this.setPassword(password);
            return this;
        }

        public Builder setMoney(Integer money) {
            User.this.setMoney(money);
            return this;
        }

        public Builder setRegistrationDate(Timestamp registrationDate) {
            User.this.setRegistrationDate(registrationDate);
            return this;
        }

        public Builder setImagePath(String imagePath) {
            User.this.setImagePath(imagePath);
            return this;
        }

        public Builder setEmail(String email) {
            User.this.setEmail(email);
            return this;
        }

        public Builder setSecurityToken(String securityToken) {
            User.this.setSecurityToken(securityToken);
            return this;
        }

        public Builder setFcmToken(String fcmToken) {
            User.this.setFcmToken(fcmToken);
            return this;
        }

        public User build() {
            return User.this;
        }
    }
}
