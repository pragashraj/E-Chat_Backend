package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.MyChat;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.MyChatRepository;
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
    private final MyChatRepository myChatRepository;
    private final String username;

    public UserContactsResponse execute() throws EntityNotFoundException {
        User user = getUser(username);

        if (user == null) {
            log.error("user not found with username: {}", username);
            throw new EntityNotFoundException("User not found");
        }

        List<MyChat> myChatList = myChatRepository.findAllByUsersContains(user);

        return new UserContactsResponse(
                user.getUsername(),
                ChatMessage.MessageType.JOIN,
                myChatList.stream().map(myChat -> new UserContactsResponse.MyChat(
                        getSecondaryContributor(myChat),
                        myChat.getChats()
                )).collect(Collectors.toList())
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }

    private String getSecondaryContributor(MyChat myChat) {
        List<User> users = myChat.getUsers();

        String contributor = "";

        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                contributor = user.getUsername();
            }
        }

        return contributor;
    }
}