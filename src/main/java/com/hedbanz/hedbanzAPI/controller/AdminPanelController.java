package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.DTO.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminPanelController {
    @GetMapping(value = "/admin")
    public ModelAndView getAdminPage(ModelAndView modelAndView){
        modelAndView.setViewName("admin_login");
        modelAndView.addObject("user", new UserDTO());
        return modelAndView;
    }

    @PostMapping(value = "/admin")
    public ModelAndView getAdminPanel(ModelAndView modelAndView){
        modelAndView.setViewName("admin_panel");
        return modelAndView;
    }
}
