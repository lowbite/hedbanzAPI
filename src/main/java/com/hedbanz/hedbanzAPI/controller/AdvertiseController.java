package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.service.AdvertiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advertise")
public class AdvertiseController {
    private final AdvertiseService advertiseService;

    @Autowired
    public AdvertiseController(AdvertiseService advertiseService) {
        this.advertiseService = advertiseService;
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<Integer> getAdvertiseType(){
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, advertiseService.getAdvertiseType());
    }

    @RequestMapping(value = "/rate", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody<Integer> getAdvertiseRate(){
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, advertiseService.getAdvertiseRate());
    }
}
