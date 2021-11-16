package com.echat.chat.models.responses;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContactsResponse {
    private String username;
    private ChatMessage.MessageType type;
    private List<MyChat> myChatList = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MyChat {
        private String secondaryContributor;
        private List<Chat> chats = new ArrayList<>();
    }
}