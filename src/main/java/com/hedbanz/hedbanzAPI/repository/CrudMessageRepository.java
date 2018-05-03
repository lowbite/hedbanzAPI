package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO;
import com.hedbanz.hedbanzAPI.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CrudMessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {
    @Query("SELECT new com.hedbanz.hedbanzAPI.entity.DTO.MessageDTO(u.id, u.login, u.imagePath, m.roomId, m.text, m.type, m.createDate) FROM Message m JOIN  m.senderUser u WHERE m.roomId = :roomId ORDER BY m.id DESC")
    Page<MessageDTO> findAllMessages(Pageable pageable, @Param("roomId") long roomId);
}
