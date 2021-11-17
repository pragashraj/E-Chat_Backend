package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.requests.DeleteChatByUserRequest;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@AllArgsConstructor
public class DeleteChatByUserUseCase {
    private static final Logger log = LoggerFactory.getLogger(DeleteChatByUserUseCase.class);

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final DeleteChatByUserRequest request;

    public UserContactsResponse execute() throws EntityNotFoundException {
        User currentUser = getUser(request.getCurrentUser());
        if (currentUser == null) {
            log.error("currentUser not found with username: {}", request.getCurrentUser());
            throw new EntityNotFoundException("current user not found");
        }

        User secondaryUser = getUser(request.getSecondaryContributor());
        if (secondaryUser == null) {
            log.error("secondaryUser not found with username: {}", request.getSecondaryContributor());
            throw new EntityNotFoundException("secondary user not found");
        }

        Set<Contact> contacts = currentUser.getContacts();

        for (Contact contact : contacts) {
            if (contact.getContactor().equals(request.getSecondaryContributor())) {
                contacts.remove(contact);
                currentUser.setContacts(contacts);
                userRepository.save(currentUser);
                contactRepository.delete(contact);
                break;
            }
        }

        GetUserMessageContactsUseCase useCase = new GetUserMessageContactsUseCase(
                userRepository,
                request.getCurrentUser()
        );

        return useCase.execute();
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }
}