package com.echat.chat.models.responses;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.MyChat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContactsResponse {
    private String joiner;
    private ChatMessage.MessageType type;
    private List<MyChat> myChatList = new ArrayList<>();
}