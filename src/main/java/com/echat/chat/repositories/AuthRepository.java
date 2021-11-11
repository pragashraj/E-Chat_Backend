package com.echat.chat.repositories;

import com.echat.chat.models.entities.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<UserAuthentication, String> {
    UserAuthentication findByEmail(String email);
}