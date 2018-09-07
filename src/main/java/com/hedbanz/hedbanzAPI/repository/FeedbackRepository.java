package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT COUNT(f) FROM Feedback f")
    long findCountOfAllFeedbacks();
}
