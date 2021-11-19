package com.echat.chat.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageRequest {
    private String sender;
    private String receiver;
    private String message;
    private String contentType;
}