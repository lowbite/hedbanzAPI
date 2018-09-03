package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.ApplicationRepository;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Application getApplication() {
        return applicationRepository.findOne(1);
    }

    @Transactional
    public Application updateVersion(Application application) {
        if(application.getVersion() == null)
            throw ExceptionFactory.create(InputError.EMPTY_VERSION_FIELD);
        Application dbApplication = applicationRepository.findOne(1);
        if(dbApplication.getVersion() >= application.getVersion())
            throw ExceptionFactory.create(InputError.INCORRECT_VERSION_FIELD);
        dbApplication.setVersion(application.getVersion());
        return applicationRepository.saveAndFlush(dbApplication);
    }
}
