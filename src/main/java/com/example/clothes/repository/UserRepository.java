package com.example.clothes.repository;

import com.example.clothes.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userName = :userName")
   Optional<User> findByUserNameWithRoles(@Param("userName") String userName);
   @Transactional
   Optional<User> findByUserName(String username);

   Optional<User> findByEmail(String email);
}
