package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminPanelController {
    private final ApplicationService applicationService;

    public AdminPanelController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping(value = "/admin")
    public ModelAndView getAdminPage(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_login");
        modelAndView.addObject("admin", Admin.AdminBuilder().build());
        return modelAndView;
    }

    @GetMapping(value = "/admin_panel")
    public ModelAndView getAdminPanel(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_panel");
        return modelAndView;
    }
}
