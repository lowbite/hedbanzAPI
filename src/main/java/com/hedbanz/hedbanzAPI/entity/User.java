package com.hedbanz.hedbanzAPI.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login", nullable = false)
    @NotNull
    private String login;

    @Column(name = "password", nullable = false)
    @NotNull
    private String password;

    @Column(name = "money", nullable = false)
    @NotNull
    private Integer money = 0;

    @Column(name = "registration_date", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registrationDate;

    @Column(name = "icon_id", nullable = false)
    @NotNull
    private Integer iconId = 0;

    @Column(name = "email", nullable = false)
    @NotNull
    private String email;

    @Column(name = "games_number", nullable = false)
    private Long gamesNumber = 0L;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(Long userId, String login, Integer money, Date registrationDate, Integer iconId, String email) {
        this.userId = userId;
        this.login = login;
        this.money = money;
        this.registrationDate = new Timestamp(registrationDate.getTime());
        this.iconId = iconId;
        this.email = email;
    }

    public User(Long userId, String login, String password, Integer money, Timestamp registrationDate, Integer iconId,
                String email, Long gamesNumber, String fcmToken,
                List<User> friends, List<Room> invitedToRooms, Set<Role> roles) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.money = money;
        this.registrationDate = registrationDate;
        this.iconId = iconId;
        this.email = email;
        this.gamesNumber = gamesNumber;
        this.fcmToken = fcmToken;
        this.friends = friends;
        this.invitedToRooms = invitedToRooms;
        this.roles = roles;
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

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    public Long getGamesNumber() {
        return gamesNumber;
    }

    public void setGamesNumber(Long gamesNumber) {
        this.gamesNumber = gamesNumber;
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

        public Builder setIconId(Integer iconId) {
            User.this.setIconId(iconId);
            return this;
        }

        public Builder setEmail(String email) {
            User.this.setEmail(email);
            return this;
        }

        public Builder setFcmToken(String fcmToken) {
            User.this.setFcmToken(fcmToken);
            return this;
        }

        public Builder setRoles(Set<Role> roles){
            User.this.setRoles(roles);
            return this;
        }

        public Builder setGamesNumber(Long gamesNumber){
            User.this.setGamesNumber(gamesNumber);
            return this;
        }

        public User build() {
            return User.this;
        }
    }
}
