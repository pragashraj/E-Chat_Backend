package com.echat.chat.usecases;

import com.echat.chat.exception.EntityNotFoundException;
import com.echat.chat.models.ChatMessage;
import com.echat.chat.models.entities.Chat;
import com.echat.chat.models.entities.Contact;
import com.echat.chat.models.entities.MyChat;
import com.echat.chat.models.entities.User;
import com.echat.chat.models.requests.NewMessageRequest;
import com.echat.chat.models.responses.MessageResponse;
import com.echat.chat.repositories.ChatRepository;
import com.echat.chat.repositories.ContactRepository;
import com.echat.chat.repositories.MyChatRepository;
import com.echat.chat.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
public class CreateNewMessageUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateNewMessageUseCase.class);

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MyChatRepository myChatRepository;
    private final ContactRepository contactRepository;
    private final NewMessageRequest request;

    public MessageResponse execute() throws EntityNotFoundException, UnsupportedEncodingException {
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
                getChat(chat),
                request.getContentType()
        );
    }

    private User getUser(String name) {
        return userRepository.findByUsername(name);
    }

    private Chat createNewChat(User sender, User receiver) throws UnsupportedEncodingException {
        String message = request.getMessage();
        String contentType = request.getContentType();

        if (contentType.equals("EMOJI")) {
            message = encodeStringUrl(message);
        }

        Chat chat = Chat.builder()
                .message(message)
                .dateTime(LocalDateTime.now())
                .sender(request.getSender())
                .senderId(sender.getId())
                .receiver(request.getReceiver())
                .receiverId(receiver.getId())
                .contentType(request.getContentType())
                .build();

        return chatRepository.save(chat);
    }

    private void handleChats(User sender, User receiver, Chat chat) {
        Set<Contact> senderContacts = sender.getContacts();

        MyChat myChat = null;

        for (Contact contact : senderContacts) {
            if (contact.getContactor().equals(receiver.getUsername())) {
                myChat = contact.getMyChat();
            }
        }

        if (myChat == null) {
            List<Chat> chatList = new ArrayList<>();
            chatList.add(chat);

            MyChat newMyChat = MyChat.builder().chats(chatList).build();
            myChatRepository.save(newMyChat);

            Contact senderContact = Contact.builder()
                    .contactor(receiver.getUsername())
                    .myChat(newMyChat)
                    .build();
            contactRepository.save(senderContact);

            senderContacts.add(senderContact);
            sender.setContacts(senderContacts);
            userRepository.save(sender);

            Contact receiverContact = Contact.builder()
                    .contactor(sender.getUsername())
                    .myChat(newMyChat)
                    .build();
            contactRepository.save(receiverContact);

            Set<Contact> receiverContacts = receiver.getContacts();
            receiverContacts.add(receiverContact);
            receiver.setContacts(receiverContacts);
            userRepository.save(receiver);
        } else {
            List<Chat> chatList = myChat.getChats();
            chatList.add(chat);

            myChat.setChats(chatList);
            myChatRepository.save(myChat);
        }
    }

    private String encodeStringUrl(String message) throws UnsupportedEncodingException {
        return URLEncoder.encode(message, "UTF-8");
    }

    private String decodeStringUrl(String message) throws UnsupportedEncodingException {
        return URLDecoder.decode(message, "UTF-8");
    }

    private Chat getChat(Chat chat) throws UnsupportedEncodingException {
        return Chat.builder()
                .id(chat.getId())
                .message(decodeStringUrl(chat.getMessage()))
                .dateTime(chat.getDateTime())
                .sender(chat.getSender())
                .receiver(chat.getReceiver())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .contentType(request.getContentType())
                .build();
    }
}