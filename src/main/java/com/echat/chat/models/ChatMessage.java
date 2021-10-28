package com.echat.chat.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String content;
    private String sender;
    private String receiver;
    private MessageType type;
    private LocalDateTime dateTime = LocalDateTime.now();

    public enum MessageType { CHAT, JOIN, LEAVE, TYPING }
}