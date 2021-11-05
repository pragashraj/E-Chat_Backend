package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GetUserMessageContactsUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetUserMessageContactsUseCase.class);

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final String username;

    public UserContactsResponse execute() throws EntityNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("user not found with username: {}", username);
            throw new EntityNotFoundException("User not found");
        }

        List<Contact> contacts = contactRepository.findAllByContactOwner(user);

        return new UserContactsResponse(
                user.getUsername(),
                ChatMessage.MessageType.JOIN,
                contacts
        );
    }
}