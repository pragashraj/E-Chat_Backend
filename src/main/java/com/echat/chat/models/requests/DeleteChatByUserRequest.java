package com.echat.chat.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteChatByUserRequest {
    private String currentUser;
    private String secondaryContributor;
}