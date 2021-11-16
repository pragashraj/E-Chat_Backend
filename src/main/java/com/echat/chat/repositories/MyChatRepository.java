package com.echat.chat.repositories;

import com.echat.chat.models.entities.MyChat;
import com.echat.chat.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MyChatRepository extends JpaRepository<MyChat, String> {

}