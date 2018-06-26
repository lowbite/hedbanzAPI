package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Application;
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
        if(application == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        if(application.getVersion() == null)
            throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

        Application dbApplication = applicationRepository.findOne(1);
        dbApplication.setVersion(application.getVersion());
        return applicationRepository.saveAndFlush(dbApplication);
    }
}
