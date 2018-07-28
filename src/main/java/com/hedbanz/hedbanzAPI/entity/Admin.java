package com.hedbanz.hedbanzAPI.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String login;
    @NotNull
    private String password;

    private String securityToken;

    public Admin() {
    }

    public Admin(Long id, String login, String password, String securityToken) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.securityToken = securityToken;
    }

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

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Admin admin = (Admin) o;

        if (id != null ? !id.equals(admin.id) : admin.id != null) return false;
        return login != null ? login.equals(admin.login) : admin.login == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

    public static Builder AdminBuilder(){
        return new Admin().new Builder();
    }

    public class Builder {
        private Builder(){

        }

        public Builder setId(Long id){
            Admin.this.setId(id);
            return this;
        }

        public Builder setLogin(String login){
            Admin.this.setLogin(login);
            return this;
        }

        public Builder setPassword(String password){
            Admin.this.setPassword(password);
            return this;
        }

        public Builder setSeurityToken(String securityToken){
            Admin.this.setSecurityToken(securityToken);
            return this;
        }

        public Admin build(){
            return Admin.this;
        }
    }
}
