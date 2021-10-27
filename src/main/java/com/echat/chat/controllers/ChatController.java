package com.echat.chat.controllers;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.utills.WebSocketEventListener;
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

import java.util.Objects;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/pubic")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try {
            return chatMessage;
        } catch (Exception e) {
            logger.error("Unable to send message, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/pubic")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put(
                    "username",
                    chatMessage.getSender()
            );
            return chatMessage;
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
        } catch (Exception e) {
            logger.error("Unable to send private message, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }

    @MessageMapping("/addPrivateUser")
    @SendTo("/queue/reply")
    public ChatMessage addPrivateUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put(
                    "private-username",
                    chatMessage.getSender()
            );
            return chatMessage;
        } catch (Exception e) {
            logger.error("Unable to add private user, cause: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
        }
    }
}