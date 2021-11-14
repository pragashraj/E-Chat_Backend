package com.echat.chat.models.responses;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.MyChat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContactsResponse {
    private String username;
    private ChatMessage.MessageType type;
    private Set<MyChat> myChatList;
}