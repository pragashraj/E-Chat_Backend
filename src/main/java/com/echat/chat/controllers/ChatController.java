package com.echat.chat.controllers;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.models.responses.UserContactsResponse;
import com.echat.chat.repositories.ChatRepository;
import com.echat.chat.repositories.MyChatRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final UserRepository userRepository;
    private final MyChatRepository myChatRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(UserRepository userRepository,
                          MyChatRepository myChatRepository,
                          ChatRepository chatRepository
    ) {
        this.userRepository = userRepository;
        this.myChatRepository = myChatRepository;
        this.chatRepository = chatRepository;
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
                        chatRepository,
                        myChatRepository,
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