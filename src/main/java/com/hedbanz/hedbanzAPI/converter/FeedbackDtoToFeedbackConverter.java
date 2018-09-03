package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.transfer.FeedbackDto;
import org.springframework.core.convert.converter.Converter;

public class FeedbackDtoToFeedbackConverter implements Converter<FeedbackDto, Feedback> {
    @Override
    public Feedback convert(FeedbackDto feedbackDto) {
        return new Feedback.Builder()
                .setDeviceManufacturer(feedbackDto.getDeviceManufacturer())
                .setDeviceModel(feedbackDto.getDeviceModel())
                .setDeviceName(feedbackDto.getDeviceName())
                .setDeviceVersion(feedbackDto.getDeviceVersion())
                .setFeedbackText(feedbackDto.getFeedbackText())
                .setProduct(feedbackDto.getProduct())
                .setUser(
                        feedbackDto.getUser() == null ? null : User.Builder()
                                                                    .setUserId(feedbackDto.getUser().getId())
                                                                    .build()
                )
                .build();
    }
}
