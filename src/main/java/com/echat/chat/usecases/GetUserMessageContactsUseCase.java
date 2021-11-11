package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class GetUserMessageContactsUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetUserMessageContactsUseCase.class);

    private final UserRepository userRepository;
    private final String username;

    public UserContactsResponse execute() throws EntityNotFoundException {
        User user = getUser(username);

        if (user == null) {
            log.error("user not found with username: {}", username);
            throw new EntityNotFoundException("User not found");
        }

        return new UserContactsResponse(
                user.getUsername(),
                ChatMessage.MessageType.JOIN,
                user.getMyChats()
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }
}