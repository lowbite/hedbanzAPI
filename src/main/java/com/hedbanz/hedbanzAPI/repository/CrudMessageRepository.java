package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CrudMessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {
    @Query("SELECT new com.hedbanz.hedbanzAPI.transfer.MessageDto(u.id, u.login, u.imagePath, m.room.id, m.text, m.type, m.createDate) FROM Message m JOIN  m.senderUser u join m.room r WHERE r.id = :roomId ORDER BY m.id DESC")
    Page<MessageDto> findAllMessages(Pageable pageable, @Param("roomId") long roomId);
}
