package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.Message;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MessageRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GetUserMessageContactsUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetUserMessageContactsUseCase.class);

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;
    private final String username;

    public UserContactsResponse execute() throws EntityNotFoundException {
        User user = getUser(username);

        if (user == null) {
            log.error("user not found with username: {}", username);
            throw new EntityNotFoundException("User not found");
        }

        List<Contact> contacts = contactRepository.findAllByContactOwner(user);

        return new UserContactsResponse(
                user.getUsername(),
                ChatMessage.MessageType.JOIN,
                contacts.stream().map(contact -> new UserContactsResponse.Contact(
                        contact.getId(),
                        getUser(contact.getContactPerson()),
                        getMessages(contact)
                )).collect(Collectors.toList())
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }

    private List<Message> getMessages(Contact contact) {
        return messageRepository.findAllByReceiverAndSender(
                getUser(contact.getContactPerson()),
                contact.getContactOwner()
        );
    }
}