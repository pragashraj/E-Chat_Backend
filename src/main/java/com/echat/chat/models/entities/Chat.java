package com.echat.chat.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generic-generator")
    @GenericGenerator(name = "generic-generator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "C"),
                    @org.hibernate.annotations.Parameter(name = "digits", value = "9"),
                    @org.hibernate.annotations.Parameter(name = "initial_id", value = "100000000"),
            },
            strategy = "com.echat.chat.utils.GenericIdGenerator")
    private String id;

    private String message;

    private LocalDateTime dateTime;

    private String sender;

    private String senderId;

    private String receiver;

    private String receiverId;
}