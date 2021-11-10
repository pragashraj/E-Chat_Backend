package com.echat.chat.models.responses;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String sender;
    private String receiver;
    private ChatMessage.MessageType type;
    private Chat chat;
}
