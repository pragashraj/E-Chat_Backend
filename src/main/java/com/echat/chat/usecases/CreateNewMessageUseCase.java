package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Chat;
import com.echat.chat.models.entities.MyChat;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.models.responses.MessageResponse;
import com.echat.chat.repositories.ChatRepository;
import com.echat.chat.repositories.MyChatRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CreateNewMessageUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateNewMessageUseCase.class);

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MyChatRepository myChatRepository;
    private final NewMessageRequest request;

    public MessageResponse execute() throws EntityNotFoundException {
        User sender = getUser(request.getSender());

        if (sender == null) {
            log.error("sender not found with username: {}", request.getSender());
            throw new EntityNotFoundException("Sender not found");
        }

        User receiver = getUser(request.getReceiver());

        if (receiver == null) {
            log.error("receiver not found with username: {}", request.getReceiver());
            throw new EntityNotFoundException("Receiver not found");
        }

        Chat chat = createNewChat(sender, receiver);
        handleChats(sender, receiver, chat);

        return new MessageResponse(
                sender.getUsername(),
                receiver.getUsername(),
                ChatMessage.MessageType.CHAT,
                chat
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }

    private Chat createNewChat(User sender, User receiver) {
        Chat chat = Chat.builder()
                .message(request.getMessage())
                .dateTime(LocalDateTime.now())
                .sender(request.getSender())
                .senderId(sender.getId())
                .receiver(request.getReceiver())
                .receiverId(receiver.getId())
                .build();

        return chatRepository.save(chat);
    }

    private MyChat checkExistence(List<MyChat> myChatList, User user) {
        for (MyChat myChat : myChatList) {
            if (myChat.getSecondaryContributor().equals(user.getUsername())) {
                return myChat;
            }
        }
        return null;
    }

    private void handleChats(User sender, User receiver, Chat chat) {
        List<MyChat> senderMyChatList = sender.getMyChats();

        if (senderMyChatList.isEmpty()) {
            createMyChat(sender, receiver, chat);
        } else {
            MyChat existingContact = checkExistence(senderMyChatList, receiver);

            if (existingContact != null) {
                List<Chat> chats = existingContact.getChats();
                chats.add(chat);
                existingContact.setChats(chats);
                myChatRepository.save(existingContact);
            } else {
                createMyChat(sender, receiver, chat);
            }
        }
    }

    private void createMyChat(User sender, User receiver, Chat chat) {
        List<MyChat> senderMyChatList = sender.getMyChats();
        List<MyChat> receiverMyChatList = receiver.getMyChats();

        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);

        MyChat myChat = MyChat.builder()
                .primaryContributor(sender.getUsername())
                .secondaryContributor(receiver.getUsername())
                .chats(chatList)
                .build();

        myChatRepository.save(myChat);

        senderMyChatList.add(myChat);
        sender.setMyChats(senderMyChatList);
        userRepository.save(sender);
    }
}