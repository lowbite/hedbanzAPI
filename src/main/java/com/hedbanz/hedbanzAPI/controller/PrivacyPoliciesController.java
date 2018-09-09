package com.hedbanz.hedbanzAPI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PrivacyPoliciesController {
    @RequestMapping(value = "/privacy-policies", method = RequestMethod.GET)
    public String getPrivacyPoliciesPage(@RequestParam("lang") String lang){
        if(lang.equals("ru"))
            return "privacy_policies_ru";
        return "privacy_policies_ru";
    }
}
