package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.service.AdminService;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AdminPanelController {
    private final AdminService adminService;
    private final ApplicationService applicationService;
    private final FcmService fcmService;
    private final UserService userService;

    @Autowired
    public AdminPanelController(AdminService adminService, ApplicationService applicationService,
                                FcmService fcmService, UserService userService) {
        this.adminService = adminService;
        this.applicationService = applicationService;
        this.fcmService = fcmService;
        this.userService = userService;
    }

    @GetMapping(value = "/admin")
    public ModelAndView getAdminPage(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_login");
        modelAndView.addObject("admin", Admin.AdminBuilder().build());
        return modelAndView;
    }

    @PostMapping(value = "/admin")
    public ModelAndView authorizeAdmin(@ModelAttribute("admin") Admin admin) {
        adminService.authorizeAdmin(admin);
        return new ModelAndView("redirect:/admin_panel");
    }

    @GetMapping(value = "/admin_panel")
    public ModelAndView getAdminPanel(ModelAndView modelAndView) {
        modelAndView.setViewName("admin_panel");
        modelAndView.addObject("app", applicationService.getApplication());
        return modelAndView;
    }

    @PostMapping(value = "/admin_panel")
    public ModelAndView updateApplicationData(@ModelAttribute("application") Application application) {
        Application newApplication = applicationService.updateVersion(application);
        List<User> users = userService.getAllUsers();
        new Thread(() -> users.forEach(user -> {
            if(user.getFcmToken() != null) {
                FcmPush.FcmPushData fcmPushData = new FcmPush.FcmPushData<>(NotificationMessageType.APP_NEW_VERSION.getCode(), newApplication);
                FcmPush fcmPush = new FcmPush.Builder()
                        .setTo(user.getFcmToken())
                        .setData(fcmPushData)
                        .setNotification(new Notification("New version of hedbanz!", "Hurry up to update your favorite application"))
                        .setPriority("normal")
                        .build();
                fcmService.sendPushNotification(fcmPush);
            }
        })).start();
        ModelAndView modelAndView = new ModelAndView("redirect:/admin_panel");
        modelAndView.addObject("app", newApplication);
        return modelAndView;
    }
}
