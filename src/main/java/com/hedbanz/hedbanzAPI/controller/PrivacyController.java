package com.hedbanz.hedbanzAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PrivacyController {
    @RequestMapping(method = RequestMethod.GET, value = "/privacy-policies")
    String getPageWithPolicies(){
        return "privacy_policies";
    }
}
