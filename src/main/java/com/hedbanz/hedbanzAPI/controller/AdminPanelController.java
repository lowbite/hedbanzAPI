package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.entity.Advertise;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.model.RoomFilter;
import com.hedbanz.hedbanzAPI.service.ApplicationService;
import com.hedbanz.hedbanzAPI.service.RoomService;
import com.hedbanz.hedbanzAPI.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminPanelController {
    private final ApplicationService applicationService;
    private final RoomService roomService;
    private final UserService userService;

    public AdminPanelController(ApplicationService applicationService, RoomService roomService, UserService userService) {
        this.applicationService = applicationService;
        this.roomService = roomService;
        this.userService = userService;
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

    @GetMapping(value = "/admin/application-version")
    public ModelAndView getAppVersionPanel(ModelAndView modelAndView) {
        Application application = applicationService.getApplication();
        modelAndView.setViewName("version_panel");
        modelAndView.addObject("version", application.getVersion());
        return modelAndView;
    }

    @GetMapping(value = "/admin/global-notification")
    public ModelAndView getGlobalNotificationPanel(ModelAndView modelAndView){
        modelAndView.setViewName("global_notification_panel");
        return modelAndView;
    }

    @GetMapping(value = "/admin/feedback-panel")
    public ModelAndView getPanelWithFeedback(ModelAndView modelAndView){
        modelAndView.setViewName("feedback_panel");
        return modelAndView;
    }

    @GetMapping(value = "/admin/game-stats")
    public ModelAndView getGameStatsPanel(ModelAndView modelAndView){
        RoomFilter roomFilter = new RoomFilter();
        roomFilter.setMinPlayers(2);
        roomFilter.setMaxPlayers(2);
        modelAndView.addObject("roomsNumberForTwo", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(3);
        roomFilter.setMaxPlayers(3);
        modelAndView.addObject("roomsNumberForThree", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(4);
        roomFilter.setMaxPlayers(4);
        modelAndView.addObject("roomsNumberForFour", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(5);
        roomFilter.setMaxPlayers(5);
        modelAndView.addObject("roomsNumberForFive", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(6);
        roomFilter.setMaxPlayers(6);
        modelAndView.addObject("roomsNumberForSix", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(7);
        roomFilter.setMaxPlayers(7);
        modelAndView.addObject("roomsNumberForSeven", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(8);
        roomFilter.setMaxPlayers(8);
        modelAndView.addObject("roomsNumberForEight", roomService.getRoomsCountByAdminFilter(roomFilter));
        roomFilter.setMinPlayers(null);
        roomFilter.setMaxPlayers(null);
        roomFilter.setIsPrivate(true);
        modelAndView.addObject("roomsNumberWithPassword", roomService.getRoomsCountByAdminFilter(roomFilter));
        modelAndView.addObject("usersNumber", userService.getUserNumber());
        modelAndView.setViewName("game_stats_panel");
        return modelAndView;
    }

    @GetMapping(value = "/admin/advertise")
    public ModelAndView getAdvertisePanel(ModelAndView modelAndView) {
        Advertise advertise = applicationService.getAdvertise();
        modelAndView.addObject("delay", advertise.getDelay());
        modelAndView.setViewName("advertise_panel");
        return modelAndView;
    }
}
