package com.echat.chat.repositories;

import com.echat.chat.models.entities.MyChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyChatRepository extends JpaRepository<MyChat, String> {
}