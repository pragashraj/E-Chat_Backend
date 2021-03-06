package com.echat.chat.controllers;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.models.responses.MessageResponse;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ChatRepository;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MyChatRepository;
import com.echat.chat.repositories.UserRepository;
import com.echat.chat.usecases.CreateNewMessageUseCase;
import com.echat.chat.usecases.GetUserMessageContactsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final UserRepository userRepository;
    private final MyChatRepository myChatRepository;
    private final ChatRepository chatRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public ChatController(UserRepository userRepository,
                          MyChatRepository myChatRepository,
                          ChatRepository chatRepository,
                          ContactRepository contactRepository
    ) {
        this.userRepository = userRepository;
        this.myChatRepository = myChatRepository;
        this.chatRepository = chatRepository;
        this.contactRepository = contactRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public MessageResponse sendMessage(@Payload ChatMessage chatMessage) {
        try {
            MessageResponse response = null;
            if (chatMessage.getType().equals(ChatMessage.MessageType.CHAT)) {
                NewMessageRequest request = new NewMessageRequest(
                        chatMessage.getSender(),
                        chatMessage.getReceiver(),
                        chatMessage.getContent()
                );
                CreateNewMessageUseCase useCase = new CreateNewMessageUseCase(
                        userRepository,
                        chatRepository,
                        myChatRepository,
                        contactRepository,
                        request
                );
                response = useCase.execute();
            }
            logger.info("New message: {}, from : {}, to: {}",
                    chatMessage.getContent(),
                    chatMessage.getSender(),
                    chatMessage.getReceiver()
            );
            return response;
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
}