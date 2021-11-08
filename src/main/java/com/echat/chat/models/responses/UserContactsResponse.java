package com.echat.chat.models.responses;

import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Message;
import com.echat.chat.models.entities.User;
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
    private String sender;
    private ChatMessage.MessageType type;
    private List<Contact> contactList = new ArrayList<>();


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Contact {
        private Long id;
        private User contactPerson;
        private List<Message> messages;
    }
}