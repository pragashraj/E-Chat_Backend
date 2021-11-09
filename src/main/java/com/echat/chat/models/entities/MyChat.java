package com.echat.chat.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String personName;

    private Long personId;

    @OneToMany
    private List<Chat> chatList;
}