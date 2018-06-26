package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.AdminRepository;
import com.hedbanz.hedbanzAPI.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public void authorizeAdmin(Admin admin) {
        if(admin.getLogin() == null)
            throw ExceptionFactory.create(UserError.EMPTY_LOGIN);
        if(admin.getPassword() == null)
            throw ExceptionFactory.create(UserError.EMPTY_PASSWORD);

        Admin foundAdmin = adminRepository.findAdminByLogin(admin.getLogin());

        if(!foundAdmin.getPassword().equals(admin.getPassword()))
            throw ExceptionFactory.create(UserError.INCORRECT_PASSWORD);
    }
}
