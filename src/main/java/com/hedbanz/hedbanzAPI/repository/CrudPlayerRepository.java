package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CrudPlayerRepository extends JpaRepository<Player, Long>, PagingAndSortingRepository<Player, Long> {
}
