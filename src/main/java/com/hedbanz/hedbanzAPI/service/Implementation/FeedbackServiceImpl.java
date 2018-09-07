package com.hedbanz.hedbanzAPI.service.Implementation;

import com.hedbanz.hedbanzAPI.constant.Constants;
import com.hedbanz.hedbanzAPI.entity.Feedback;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.error.NotFoundError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.FeedbackRepository;
import com.hedbanz.hedbanzAPI.repository.UserRepository;
import com.hedbanz.hedbanzAPI.service.FeedbackService;
import org.apache.http.util.TextUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(UserRepository userRepository, FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public void saveFeedback(Feedback feedback) {
        if(TextUtils.isEmpty(feedback.getFeedbackText()))
            throw ExceptionFactory.create(InputError.EMPTY_FEEDBACK_TEXT);
        if(feedback.getUser() == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);
        if(feedback.getUser().getUserId() == null)
            throw ExceptionFactory.create(InputError.EMPTY_USER_ID);

        User user = userRepository.findOne(feedback.getUser().getUserId());
        if(user == null)
            throw ExceptionFactory.create(NotFoundError.NO_SUCH_USER);

        feedback.setUser(user);
        feedbackRepository.save(feedback);
    }

    @Transactional
    public Long getNumberOfFeedbackRecords() {
        return feedbackRepository.findCountOfAllFeedbacks();
    }

    @Transactional
    public List<Feedback> getPageOfFeedbackRecords(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, Constants.FEEDBACK_PAGE_SIZE, Sort.Direction.DESC, "createdAt");
        Page<Feedback> page = feedbackRepository.findAll(pageable);
        return page.getContent();
    }
}
