package com.hedbanz.hedbanzAPI.repositorie;


import com.hedbanz.hedbanzAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryFunctional{
}
