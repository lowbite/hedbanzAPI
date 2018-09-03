package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.Application;
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

    @GetMapping(value = "/admin/login")
    public ModelAndView getAdminPage(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_login");
        return modelAndView;
    }

    @GetMapping(value = "/admin/panel")
    public ModelAndView getAdminPanel(ModelAndView modelAndView) {
        Application application = applicationService.getApplication();
        modelAndView.setViewName("admin_panel");
        modelAndView.addObject("version", application.getVersion());
        return modelAndView;
    }
}
