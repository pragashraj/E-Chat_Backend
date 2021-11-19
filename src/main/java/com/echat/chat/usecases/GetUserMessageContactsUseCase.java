package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Chat;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                user.getContacts().stream().map(contact -> new UserContactsResponse.MyChat(
                        contact.getContactor(),
                        getChats(contact.getMyChat().getChats())
                )).collect(Collectors.toList())
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }

    @SneakyThrows
    private List<Chat> getChats(List<Chat> chats) {
        List<Chat> chatList = new ArrayList<>();

        for (Chat chat : chats) {
            chatList.add(getChat(chat));
        }

        return chatList;
    }

    private Chat getChat(Chat chat) throws UnsupportedEncodingException {
        return Chat.builder()
                .id(chat.getId())
                .message(decodeStringUrl(chat.getMessage()))
                .dateTime(chat.getDateTime())
                .sender(chat.getSender())
                .receiver(chat.getReceiver())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .contentType(chat.getContentType())
                .build();
    }

    private String decodeStringUrl(String message) throws UnsupportedEncodingException {
        return URLDecoder.decode(message, "UTF-8");
    }
}