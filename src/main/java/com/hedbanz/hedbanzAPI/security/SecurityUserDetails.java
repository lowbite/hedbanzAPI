package com.hedbanz.hedbanzAPI.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class SecurityUserDetails implements UserDetails {
    private static final long serialVersionUID = 2396654715019746670L;

    private Long id;
    private String username;
    private String password;
    private String token;

    @JsonCreator
    public SecurityUserDetails(Long id, String username, String password, String token) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.token = token;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public static SecurityUserDetails from(User user) {
        if(user != null)
            return new SecurityUserDetailsBuilder()
                                            .setId(user.getUserId())
                                            .setUsername(user.getLogin())
                                            .setPassword(user.getPassword())
                                            .setToken(user.getSecurityToken())
                                            .build();
        else
            return null;
    }

    public static class SecurityUserDetailsBuilder {
        private Long id;
        private String username;
        private String password;
        private String token;

        public SecurityUserDetailsBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public SecurityUserDetailsBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public SecurityUserDetailsBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public SecurityUserDetailsBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public SecurityUserDetails build() {
            return new SecurityUserDetails(id, username, password, token);
        }
    }
}
