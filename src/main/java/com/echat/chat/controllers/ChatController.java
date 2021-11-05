package com.echat.chat.controllers;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MessageRepository;
import com.echat.chat.repositories.UserRepository;
import com.echat.chat.usecases.CreateNewMessageUseCase;
import com.echat.chat.usecases.GetUserMessageContactsUseCase;
import com.echat.chat.utils.WebSocketEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          UserRepository userRepository,
                          MessageRepository messageRepository,
                          ContactRepository contactRepository
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try {
            if (chatMessage.getType().equals(ChatMessage.MessageType.CHAT)) {
                NewMessageRequest request = new NewMessageRequest(
                        chatMessage.getSender(),
                        chatMessage.getReceiver(),
                        chatMessage.getContent()
                );
                CreateNewMessageUseCase useCase = new CreateNewMessageUseCase(
                        userRepository,
                        messageRepository,
                        contactRepository,
                        request
                );
                useCase.execute();
            }
            logger.info("New message: {}, from : {}, to: {}",
                    chatMessage.getContent(),
                    chatMessage.getSender(),
                    chatMessage.getReceiver()
            );
            return chatMessage;
        } catch (EntityNotFoundException e) {
            logger.error("Unable to send message, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Unable to send message, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public UserContactsResponse addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            headerAccessor.getSessionAttributes().put(
                    "username",
                    chatMessage.getSender()
            );
            GetUserMessageContactsUseCase useCase = new GetUserMessageContactsUseCase(
                    userRepository,
                    contactRepository,
                    chatMessage.getSender()
            );
            UserContactsResponse response = useCase.execute();
            logger.info("New user: {} added", chatMessage.getSender());
            return response;
        } catch (EntityNotFoundException e) {
            logger.error("Unable to add user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Unable to add user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }

    @MessageMapping("/sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        try {
            simpMessagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiver().trim(),
                    "/reply",
                    chatMessage
            );
            logger.info("New private message: {}, from : {}, to: {}",
                    chatMessage.getContent(),
                    chatMessage.getSender(),
                    chatMessage.getReceiver()
            );
        } catch (Exception e) {
            logger.error("Unable to send private message, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }

    @MessageMapping("/addPrivateUser")
    @SendTo("/queue/reply")
    public ChatMessage addPrivateUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            headerAccessor.getSessionAttributes().put(
                    "private-username",
                    chatMessage.getSender()
            );
            logger.info("New private user: {} added", chatMessage.getSender());
            return chatMessage;
        } catch (Exception e) {
            logger.error("Unable to add private user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }
}