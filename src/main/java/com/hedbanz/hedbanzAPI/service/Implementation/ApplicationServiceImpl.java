package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.entity.Advertise;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.AdvertiseRepository;
import com.hedbanz.hedbanzAPI.repository.ApplicationRepository;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepository applicationRepository;
    private final AdvertiseRepository advertiseRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, AdvertiseRepository advertiseRepository) {
        this.applicationRepository = applicationRepository;
        this.advertiseRepository = advertiseRepository;
    }

    @Override
    public Application getApplication() {
        return applicationRepository.findById(1).orElse(null);
    }

    @Transactional
    public Application updateVersion(Application application) {
        if(application.getVersion() == null)
            throw ExceptionFactory.create(InputError.EMPTY_VERSION_FIELD);
        Application dbApplication = applicationRepository.findById(1).get();
        if(dbApplication.getVersion() >= application.getVersion())
            throw ExceptionFactory.create(InputError.INCORRECT_VERSION_FIELD);
        dbApplication.setVersion(application.getVersion());
        return applicationRepository.saveAndFlush(dbApplication);
    }

    @Override
    public Advertise getAdvertise() {
        return advertiseRepository.findById(1L).orElse(null);
    }

    @Transactional
    public Advertise updateAdvertise(Advertise advertise) {
        if(advertise.getDelay() == null)
            throw ExceptionFactory.create(InputError.EMPTY_ADVERTISE_DELAY);
        if(advertise.getType() == null)
            throw ExceptionFactory.create(InputError.EMPTY_ADVERTISE_TYPE);
        Advertise currentAdvertise = advertiseRepository.findById(1L).get();
        currentAdvertise.setDelay(advertise.getDelay());
        currentAdvertise.setType(advertise.getType());
        return advertiseRepository.save(currentAdvertise);
    }
}
