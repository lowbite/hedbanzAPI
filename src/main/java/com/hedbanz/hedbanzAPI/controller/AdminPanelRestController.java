package com.hedbanz.hedbanzAPI.controller;

import com.hedbanz.hedbanzAPI.builder.AppNewVersionFcmPushBuilder;
import com.hedbanz.hedbanzAPI.builder.FcmPushDirector;
import com.hedbanz.hedbanzAPI.builder.GlobalFcmPushBuilder;
import com.hedbanz.hedbanzAPI.constant.NotificationMessageType;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.entity.Advertise;
import com.hedbanz.hedbanzAPI.entity.Application;
import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.model.Notification;
import com.hedbanz.hedbanzAPI.service.*;
import com.hedbanz.hedbanzAPI.transfer.FeedbackDto;
import com.hedbanz.hedbanzAPI.transfer.GlobalNotificationDto;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.hedbanz.hedbanzAPI.constant.Constants.FEEDBACK_PAGE_SIZE;
import static com.hedbanz.hedbanzAPI.constant.Constants.ROOM_PAGE_SIZE;

@RestController
public class AdminPanelRestController {
    private final ApplicationService applicationService;
    private final FcmService fcmService;
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final ConversionService conversionService;

    @Autowired
    public AdminPanelRestController(ApplicationService applicationService,
                                    FcmService fcmService, UserService userService, FeedbackService feedbackService,
                                    @Qualifier("APIConversionService") ConversionService conversionService) {
        this.applicationService = applicationService;
        this.fcmService = fcmService;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.conversionService = conversionService;
    }

    @GetMapping(value = "/admin/panel/app")
    public ResponseBody<Application> getApplicationData() {
        Application application = applicationService.getApplication();
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, application);
    }

    @PostMapping(value = "/admin/panel/app")
    public ResponseBody<Application> updateApplicationData(@RequestBody Application application) {
        Application newApplication = applicationService.updateVersion(application);
        List<User> users = userService.getAllUsers();
        new Thread(() -> users.forEach(user -> {
            if (user.getFcmToken() != null) {
                FcmPush fcmPush = new FcmPushDirector(new AppNewVersionFcmPushBuilder())
                        .buildFcmPush(user.getFcmToken(), newApplication);
                fcmService.sendPushNotification(fcmPush);
            }
        })).start();
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, newApplication);
    }

    @PostMapping(value = "/admin/notification/send")
    public ResponseBody<?> sendGlobalNotification(@RequestBody GlobalNotificationDto globalNotificationDto) {
        if (TextUtils.isEmpty(globalNotificationDto.getText()))
            throw ExceptionFactory.create(InputError.EMPTY_MESSAGE_TEXT);
        FcmPush fcmPush = new FcmPushDirector(new GlobalFcmPushBuilder())
                .buildFcmPush(null, globalNotificationDto.getText()
                );
        List<String> fcmTokens = userService.getAllFcmTokens();
        fcmService.sendPushNotificationsToUsers(fcmPush, fcmTokens);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }

    @GetMapping(value = "/admin/feedback")
    public ResponseBody<List<FeedbackDto>> getListOfFeedback(@RequestParam("page") int pageNumber) {
        List<Feedback> feedbackList = feedbackService.getPageOfFeedbackRecords(pageNumber);
        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            feedbackDtos.add(conversionService.convert(feedback, FeedbackDto.class));
        }
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, feedbackDtos);
    }

    @GetMapping(value = "/admin/feedback/pages-number")
    public ResponseBody<Long> getNumberOfFeedbackPages() {
        long records = feedbackService.getNumberOfFeedbackRecords();
        long left = records % FEEDBACK_PAGE_SIZE == 0 ? 0 : 1;
        Long pages = records / FEEDBACK_PAGE_SIZE + left;
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, pages);
    }

    @PostMapping(value = "/admin/advertise")
    public ResponseBody<?> setAdvertiseSettings(@RequestBody Advertise advertise) {
        applicationService.updateAdvertise(advertise);
        return new ResponseBody<>(ResultStatus.SUCCESS_STATUS, null, null);
    }
}
