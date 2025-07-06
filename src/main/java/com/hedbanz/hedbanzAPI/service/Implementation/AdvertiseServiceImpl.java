package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.repository.AdvertiseRepository;
import com.hedbanz.hedbanzAPI.service.AdvertiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdvertiseServiceImpl implements AdvertiseService {
    private final AdvertiseRepository advertiseRepository;

    @Autowired
    public AdvertiseServiceImpl(AdvertiseRepository advertiseRepository) {
        this.advertiseRepository = advertiseRepository;
    }

    @Transactional
    public Integer getAdvertiseType() {
        return advertiseRepository.findById(1L).get().getType();
    }

    @Transactional
    public Integer getAdvertiseRate() {
        return advertiseRepository.findById(1L).get().getDelay();
    }
}
