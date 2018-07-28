package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Admin;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AdminService {
    Admin authorizeAdmin(Admin admin);
    Optional<UserDetails> findAdminByToken(String token);
}
