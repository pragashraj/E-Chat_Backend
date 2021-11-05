package com.echat.chat.repositories;

import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByContactOwner(User user);
}