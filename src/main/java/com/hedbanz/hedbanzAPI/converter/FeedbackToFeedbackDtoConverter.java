package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.transfer.FeedbackDto;
import org.springframework.core.convert.converter.Converter;

public class FeedbackToFeedbackDtoConverter implements Converter<Feedback, FeedbackDto> {
    @Override
    public FeedbackDto convert(Feedback feedback) {
        UserToUserDtoConverter toUserDtoConverter = new UserToUserDtoConverter();
        return new FeedbackDto.Builder()
                .setDeviceManufacturer(feedback.getDeviceManufacturer())
                .setDeviceModel(feedback.getDeviceModel())
                .setDeviceName(feedback.getDeviceName())
                .setDeviceVersion(feedback.getDeviceVersion())
                .setFeedbackText(feedback.getFeedbackText())
                .setProduct(feedback.getProduct())
                .setUser(toUserDtoConverter.convert(feedback.getUser()))
                .setCreatedAt(feedback.getCreatedAt().toString())
                .build();
    }
}
