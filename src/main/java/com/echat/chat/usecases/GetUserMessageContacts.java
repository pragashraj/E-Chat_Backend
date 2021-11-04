package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.User;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MessageRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@AllArgsConstructor
public class GetUserMessageContacts {
    private static final Logger log = LoggerFactory.getLogger(GetUserMessageContacts.class);

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;
    private final String username;

    public void execute() throws EntityNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("user not found with username: {}", username);
            throw new EntityNotFoundException("User not found");
        }

        List<Contact> contacts = user.getContacts();
    }
}