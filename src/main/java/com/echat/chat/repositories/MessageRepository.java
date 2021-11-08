package com.echat.chat.repositories;

import com.echat.chat.models.entities.Message;
import com.echat.chat.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByReceiverAndSender(User receiver, User sender);
}