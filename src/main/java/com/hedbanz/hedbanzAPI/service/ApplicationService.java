package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Advertise;
import com.hedbanz.hedbanzAPI.entity.Application;

public interface ApplicationService {
    Application getApplication();
    Application updateVersion(Application application);
    Advertise getAdvertise();
    Advertise updateAdvertise(Advertise advertise);
}
