package com.hedbanz.hedbanzAPI.security;

import com.hedbanz.hedbanzAPI.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private AdminService adminService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        // Nothing to do
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
        final Object token = authenticationToken.getCredentials();
        return Optional
                .ofNullable(token)
                .map(String::valueOf)
                .flatMap(adminService::findAdminByToken)
                .orElseThrow(()-> new UsernameNotFoundException("Cannot find user with authentication token=" + token));// TODO create normal exception throwing
    }
}
