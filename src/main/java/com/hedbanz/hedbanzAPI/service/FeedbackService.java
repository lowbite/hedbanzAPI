package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    void saveFeedback(Feedback feedback);
    Long getNumberOfFeedbackRecords();
    List<Feedback> getPageOfFeedbackRecords(int pageNumber);
}
