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

    public Admin() {
    }

    public Admin(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
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

    public static AdminBuilder AdminBuilder(){
        return new Admin().new AdminBuilder();
    }

    public class AdminBuilder{
        private AdminBuilder(){

        }

        public AdminBuilder setId(Long id){
            Admin.this.setId(id);
            return this;
        }

        public AdminBuilder setLogin(String login){
            Admin.this.setLogin(login);
            return this;
        }

        public AdminBuilder setPassword(String password){
            Admin.this.setPassword(password);
            return this;
        }

        public Admin build(){
            return Admin.this;
        }
    }
}
