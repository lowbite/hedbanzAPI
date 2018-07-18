package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;

public interface MessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {
    @Query("SELECT m FROM Message m join m.room r WHERE r.id = :roomId ORDER BY m.id DESC")
    Page<Message> findAllMessages(Pageable pageable, @Param("roomId") long roomId);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("SELECT q FROM Message m JOIN m.question q JOIN m.room r WHERE r.id = :roomId ORDER BY q.id DESC")
    Page<Question> findLastQuestionByRoomId(@Param("roomId") long roomId, Pageable pageable);

    @Query("SELECT m FROM Message m JOIN m.question q WHERE q.id = :questionId")
    Message findMessageByQuestionId(@Param("questionId") long questionId);

    @Query("SELECT m FROM Message m  JOIN m.room r JOIN m.senderUser u WHERE r.id = :roomId AND u.id = :senderId AND m.type = 'WORD_SETTING'")
    Message findMessageByWordSettingType(@Param("senderId") long senderId, @Param("roomId") long roomId);

    @Modifying
    int deleteAllByRoom_Id(long roomId);
}
