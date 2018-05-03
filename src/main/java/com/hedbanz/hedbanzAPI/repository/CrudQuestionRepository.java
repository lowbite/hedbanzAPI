package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CrudQuestionRepository extends JpaRepository<Question, Long>, PagingAndSortingRepository<Question, Long> {

}
