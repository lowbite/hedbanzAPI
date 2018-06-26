package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.service.AdminService;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminPanelController {
    private final AdminService adminService;
    private final ApplicationService applicationService;

    @Autowired
    public AdminPanelController(AdminService adminService, ApplicationService applicationService) {
        this.adminService = adminService;
        this.applicationService = applicationService;
    }

    @GetMapping(value = "/admin")
    public ModelAndView getAdminPage(ModelAndView modelAndView){
        modelAndView.setViewName("admin_login");
        modelAndView.addObject("admin", Admin.AdminBuilder().build());
        return modelAndView;
    }

    @PostMapping(value = "/admin")
    public ModelAndView authorizeAdmin(@ModelAttribute("admin") Admin admin){
        adminService.authorizeAdmin(admin);
        return new ModelAndView("redirect:/admin_panel");
    }

    @GetMapping(value = "/admin_panel")
    public ModelAndView getAdminPanel(ModelAndView modelAndView){
        modelAndView.setViewName("admin_panel");
        modelAndView.addObject("app", applicationService.getApplication());
        return modelAndView;
    }

    @PostMapping(value = "/admin_panel")
    public ModelAndView updateApplicationData(@ModelAttribute("application")Application application){
        application = applicationService.updateVersion(application);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin_panel");
        modelAndView.addObject("app", application);
        return modelAndView;
    }
}
