package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Admin;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.model.CustomResponseBody;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.service.AdminService;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import com.hedbanz.hedbanzAPI.service.FcmService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class AdminPanelRestController {
    private final AdminService adminService;
    private final ApplicationService applicationService;
    private final FcmService fcmService;
    private final UserService userService;

    @Autowired
    public AdminPanelRestController(AdminService adminService, ApplicationService applicationService,
                                    FcmService fcmService, UserService userService) {
        this.adminService = adminService;
        this.applicationService = applicationService;
        this.fcmService = fcmService;
        this.userService = userService;
    }

    @PostMapping(value = "/admin")
    public CustomResponseBody<Admin> authorizeAdmin(@RequestBody Admin admin) {
        admin = adminService.authorizeAdmin(admin);
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, admin);
    }

    @GetMapping(value = "/admin_panel/app")
    public CustomResponseBody<Application> getApplicationData(){
        Application application = applicationService.getApplication();
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, application);
    }

    @PostMapping(value = "/admin_panel/app")
    public CustomResponseBody<Application> updateApplicationData(@RequestBody Application application) {
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
        return new CustomResponseBody<>(ResultStatus.SUCCESS_STATUS, null, newApplication);
    }
}
