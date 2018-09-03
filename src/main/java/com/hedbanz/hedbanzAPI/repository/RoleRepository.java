package com.hedbanz.hedbanzAPI.repository;

import com.hedbanz.hedbanzAPI.constant.RoleName;
import com.hedbanz.hedbanzAPI.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
