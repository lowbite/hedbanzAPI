package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.AdminRepository;
import com.hedbanz.hedbanzAPI.security.SecurityUserDetails;
import com.hedbanz.hedbanzAPI.service.AdminService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Admin authorizeAdmin(Admin admin) {
        if(TextUtils.isEmpty(admin.getLogin()))
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(TextUtils.isEmpty(admin.getPassword()))
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        Admin foundAdmin = adminRepository.findAdminByLogin(admin.getLogin());
        if(foundAdmin == null)
            throw ExceptionFactory.create(UserError.INCORRECT_CREDENTIALS);
        if(!foundAdmin.getPassword().equals(admin.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_CREDENTIALS);
        final String token = UUID.randomUUID().toString();
        foundAdmin.setSecurityToken(token);
        return adminRepository.saveAndFlush(foundAdmin);
    }

    @Transactional
    public Optional<UserDetails> findAdminByToken(String token) {
        Admin admin = adminRepository.findAdminBySecurityToken(token);
        return Optional.ofNullable(SecurityUserDetails.from(admin));
    }
}
