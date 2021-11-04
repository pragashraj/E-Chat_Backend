package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.Message;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MessageRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class CreateNewMessageUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateNewMessageUseCase.class);

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;
    private final NewMessageRequest request;

    public void execute() throws EntityNotFoundException {
        User sender = userRepository.findByUsername(request.getSender());

        if (sender == null) {
            log.error("sender not found with username: {}", request.getSender());
            throw new EntityNotFoundException("Sender not found");
        }

        User receiver = userRepository.findByUsername(request.getReceiver());

        if (receiver == null) {
            log.error("receiver not found with username: {}", request.getReceiver());
            throw new EntityNotFoundException("Receiver not found");
        }

        Message message = Message.builder()
                .message(request.getMessage())
                .sender(sender)
                .receiver(receiver)
                .dateTime(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        List<Contact> contacts = sender.getContacts();
        boolean receiverAlreadyExistAsContact = false;

        for (Contact contact : contacts) {
            if (contact.getContactUser() == receiver) {
                receiverAlreadyExistAsContact = true;
                break;
            }
        }

        if (!receiverAlreadyExistAsContact) {
            Contact contact = Contact.builder()
                    .contactUser(receiver)
                    .build();

            contactRepository.save(contact);
        }
    }
}