package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.entities.Chat;
import com.echat.chat.models.entities.MyChat;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.requests.NewMessageRequest;
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

    public void execute() throws EntityNotFoundException {
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
        handleChats(receiver, sender, chat);
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

    private MyChat createNewMyChat(User receiver, Chat chat) {
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);

        MyChat myChat = MyChat.builder()
                .personId(receiver.getId())
                .personName(receiver.getUsername())
                .chatList(chatList)
                .build();

        return myChatRepository.save(myChat);
    }

    private void handleChats(User sender, User receiver, Chat chat) {
        List<MyChat> myChatList = sender.getMyChatList();

        if (myChatList.isEmpty()) {
            MyChat myChat = createNewMyChat(receiver, chat);
            myChatList.add(myChat);
        } else {
            boolean contactExist = false;
            MyChat existingContact = null;

            for (MyChat myChat : myChatList) {
                if (myChat.getPersonName().equals(receiver.getUsername())) {
                    contactExist = true;
                    existingContact = myChat;
                    break;
                }
            }

            if (contactExist) {
                List<Chat> chats = existingContact.getChatList();
                chats.add(chat);
                existingContact.setChatList(chats);
                myChatRepository.save(existingContact);
                myChatList.add(existingContact);
            } else {
                MyChat myChat = createNewMyChat(receiver, chat);
                myChatList.add(myChat);
            }
        }

        sender.setMyChatList(myChatList);
        userRepository.save(sender);
    }
}